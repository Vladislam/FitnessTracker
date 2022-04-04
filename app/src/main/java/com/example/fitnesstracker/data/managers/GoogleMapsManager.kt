package com.example.fitnesstracker.data.managers

import android.graphics.Color
import com.example.fitnesstracker.util.Polyline
import com.example.fitnesstracker.util.TrackingUtility
import com.example.fitnesstracker.util.const.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleMapsManager @Inject constructor(

) {
    var map: GoogleMap? = null

    var pathPoints = mutableListOf<Polyline>()

    fun distanceInMeters(): Int = pathPoints.sumOf {
        TrackingUtility.calculatePolylineLength(it).toInt()
    }

    fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    Constants.MAP_ZOOM
                )
            )
        }
    }

    fun drawAllPolylines() {
        for (polyline in pathPoints) {
            map?.addPolyline(
                PolylineOptions()
                    .color(Color.RED)
                    .width(Constants.POLYLINE_WIDTH)
                    .addAll(polyline)
            )
        }
    }

    fun drawLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            map?.addPolyline(
                PolylineOptions()
                    .color(Color.RED)
                    .width(Constants.POLYLINE_WIDTH)
                    .add(
                        preLastLatLng,
                        lastLatLng,
                    )
            )
        }
    }

    fun zoomToSeeTheWholeRun(width: Int, height: Int) {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (position in polyline) {
                bounds.include(position)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                width,
                height,
                (height * 0.05f).toInt()
            )
        )
    }
}