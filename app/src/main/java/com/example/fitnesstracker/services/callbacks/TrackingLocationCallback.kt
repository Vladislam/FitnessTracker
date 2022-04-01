package com.example.fitnesstracker.services.callbacks

import android.location.Location
import com.example.fitnesstracker.data.models.ServiceState
import com.example.fitnesstracker.services.TrackingService
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

class TrackingLocationCallback(private val callback: (Location) -> Unit) : LocationCallback() {
    override fun onLocationResult(result: LocationResult) {
        super.onLocationResult(result)
        if (TrackingService.serviceState.value is ServiceState.Running) {
            for (location in result.locations) {
                callback.invoke(location)
            }
        }
    }
}