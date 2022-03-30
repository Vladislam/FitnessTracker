package com.example.fitnesstracker.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.example.fitnesstracker.R
import com.example.fitnesstracker.ui.MainActivity
import com.example.fitnesstracker.util.const.Constants.ACTION_PAUSE_SERVICE
import com.example.fitnesstracker.util.const.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.fitnesstracker.util.const.Constants.ACTION_START_SERVICE
import com.example.fitnesstracker.util.const.Constants.ACTION_STOP_SERVICE
import com.example.fitnesstracker.util.const.Constants.NOTIFICATION_CHANNEL_ID
import com.example.fitnesstracker.util.const.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.fitnesstracker.util.const.Constants.NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TrackingService : LifecycleService() {

    companion object {
        var isServiceRunning = false
    }

    private var isFirstRun = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Resume service")
                    }
                    isServiceRunning = true
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused service")
                    isServiceRunning = false
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Stopped service")
                    isServiceRunning = false
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
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