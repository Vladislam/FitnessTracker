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
import com.example.fitnesstracker.services.callbacks.TrackingLocationCallback
import com.example.fitnesstracker.ui.MainActivity
import com.example.fitnesstracker.util.Polylines
import com.example.fitnesstracker.util.TrackingUtility
import com.example.fitnesstracker.util.const.Constants.ACTION_PAUSE_SERVICE
import com.example.fitnesstracker.util.const.Constants.ACTION_SHOW_TRACKING_FRAGMENT
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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    private var isFirstRun = true
    private val locationCallback = TrackingLocationCallback {
        addPathPoint(it)
    }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        var serviceState = MutableLiveData<ServiceState>(ServiceState.Stopped)

        val pathPoints = MutableLiveData<Polylines>(mutableListOf())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_SERVICE -> {
                    if (isFirstRun) {
                        isFirstRun = false
                    } else {
                        Timber.d("Resume service")
                    }
                    startForegroundService()
                    serviceState.postValue(ServiceState.Running)
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused service")
                    serviceState.postValue(ServiceState.Paused)
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service")
                    stopSelf()
                    serviceState.postValue(ServiceState.Stopped)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        serviceState.observe(this) {
            updateLocationTracking(it is ServiceState.Running)
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

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForegroundService() {
        addEmptyPolyline()

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentTitle(getString(R.string.fitness_tracker))
            .setContentText(getString(R.string.zeros))
            .setContentIntent(getPendingIntent())
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getPendingIntent() = getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also { it.action = ACTION_SHOW_TRACKING_FRAGMENT },
        FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}