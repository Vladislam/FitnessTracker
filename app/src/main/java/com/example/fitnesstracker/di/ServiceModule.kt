package com.example.fitnesstracker.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.fitnesstracker.R
import com.example.fitnesstracker.ui.MainActivity
import com.example.fitnesstracker.util.const.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    @Provides
    @ServiceScoped
    fun provideNotificationBuilder(
        @ApplicationContext context: Context,
        pendingIntent: PendingIntent,
    ): NotificationCompat.Builder =
        NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_run)
            .setContentTitle(context.getString(R.string.fitness_tracker))
            .setContentText(context.getString(R.string.zeros))
            .setContentIntent(pendingIntent)

    @Provides
    @ServiceScoped
    fun providePendingIntent(
        @ApplicationContext context: Context
    ): PendingIntent = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}