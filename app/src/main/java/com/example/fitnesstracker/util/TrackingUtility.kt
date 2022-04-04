package com.example.fitnesstracker.util

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

object TrackingUtility {

    fun hasLocationPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    fun calculatePolylineLength(polyline: Polyline): Float {
        var distance = 0f
        for (i in 0..polyline.size - 2) {
            val start = polyline[i]
            val end = polyline[i + 1]

            val result = FloatArray(1)
            Location.distanceBetween(
                start.latitude,
                start.longitude,
                end.latitude,
                end.longitude,
                result
            )
            distance += result[0]
        }
        return distance
    }

    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false): String {
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        return if (!includeMillis) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, milliseconds / 10)
        }
    }

    fun getByteArrayFromBitmap(bitmap: Bitmap?): ByteArray {
        ByteArrayOutputStream().also {
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, it)
            return it.toByteArray()
        }
    }
}