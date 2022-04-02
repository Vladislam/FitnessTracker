package com.example.fitnesstracker.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.models.ServiceState
import com.example.fitnesstracker.services.callbacks.Timer
import com.example.fitnesstracker.services.callbacks.TrackingLocationCallback
import com.example.fitnesstracker.util.Polylines
import com.example.fitnesstracker.util.TrackingUtility
import com.example.fitnesstracker.util.const.Constants.ACTION_PAUSE_SERVICE
import com.example.fitnesstracker.util.const.Constants.ACTION_START_SERVICE
import com.example.fitnesstracker.util.const.Constants.ACTION_STOP_SERVICE
import com.example.fitnesstracker.util.const.Constants.FASTEST_LOCATION_INTERVAL
import com.example.fitnesstracker.util.const.Constants.LOCATION_UPDATE_INTERVAL
import com.example.fitnesstracker.util.const.Constants.NOTIFICATION_CHANNEL_ID
import com.example.fitnesstracker.util.const.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.fitnesstracker.util.const.Constants.NOTIFICATION_ID
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    private val timer = Timer()
    private var isFirstRun = true
    private var isServiceKilled = false

    private val locationCallback = TrackingLocationCallback { addPathPoint(it) }

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var curNotificationBuilder: NotificationCompat.Builder
    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        val timeRunInMillis = MutableLiveData(0L)
        var serviceState = MutableLiveData<ServiceState>(ServiceState.Stopped)
        val pathPoints = MutableLiveData<Polylines>(mutableListOf())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_SERVICE -> {
                    serviceState.postValue(ServiceState.Running)
                    if (isFirstRun) {
                        isFirstRun = false
                        startForegroundService()
                    } else {
                        Timber.d("Resume service")
                    }
                    timer.startTimer()
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused service")
                    timer.isTimerEnabled = false
                    serviceState.postValue(ServiceState.Paused)
                }
                ACTION_STOP_SERVICE -> {
                    killService()
                    Timber.d("Stopped service")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        curNotificationBuilder = notificationBuilder

        serviceState.observe(this) {
            updateLocationTracking(it is ServiceState.Running)
            updateNotification(it is ServiceState.Running)
        }
    }

    private fun updateNotification(isTracking: Boolean) {
        val notificationActionText = getString(if (isTracking) R.string.pause else R.string.resume)
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            getService(this, 1, pauseIntent, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_SERVICE
            }
            getService(this, 2, resumeIntent, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
        }

        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }
        if (!isServiceKilled) {
            curNotificationBuilder = notificationBuilder
                .addAction(R.drawable.ic_pause, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, curNotificationBuilder.build())
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermissions(this)) {
                val request: LocationRequest = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
                Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude}")
            }
        }
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
        timer.timeRunInSeconds.observe(this) {
            if (!isServiceKilled) {
                val notification = curNotificationBuilder
                    .setContentText(TrackingUtility.getFormattedStopWatchTime(it * 1000))
                    .build()
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun postInitialValues() {
        serviceState.postValue(ServiceState.Stopped)
        pathPoints.postValue(mutableListOf())
        timer.timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    private fun killService() {
        isServiceKilled = true
        isFirstRun = true
        timer.isTimerEnabled = false
        stopForeground(true)
        stopSelf()
        postInitialValues()
        serviceState.postValue(ServiceState.Stopped)
    }
}