package com.example.fitnesstracker.data.managers

import android.content.Context
import android.content.IntentSender.SendIntentException
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.example.fitnesstracker.R
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import timber.log.Timber


class GpsManager(
    private val resolutionForResult: ActivityResultLauncher<IntentSenderRequest>,
    private val context: Context,
    private val action: ((Boolean) -> Unit)
) {

    fun turnOnGPS() {
        val request = LocationRequest.create().apply {
            interval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(request).build()
        LocationServices.getSettingsClient(context)
            .checkLocationSettings(builder)
            .addOnSuccessListener {
                action.invoke(true)
            }
            .addOnFailureListener { e ->
                when ((e as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val intentSenderRequest =
                            IntentSenderRequest.Builder((e as ResolvableApiException).resolution)
                                .build()
                        resolutionForResult.launch(intentSenderRequest)
                    } catch (sie: SendIntentException) {
                        Timber.e("PendingIntent unable to execute request.")
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage = context.getString(R.string.error_gps_unavailable)
                        Timber.e(errorMessage)
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }

    }
}