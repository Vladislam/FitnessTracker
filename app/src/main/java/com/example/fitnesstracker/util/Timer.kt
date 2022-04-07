package com.example.fitnesstracker.util

import androidx.lifecycle.MutableLiveData
import com.example.fitnesstracker.data.models.ServiceState
import com.example.fitnesstracker.services.TrackingService
import com.example.fitnesstracker.util.const.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Timer {
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L
    val timeRunInSeconds = MutableLiveData(0L)
    var isTimerEnabled = false

    fun startTimer() {
        addEmptyPolyline()
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while (TrackingService.serviceState.value is ServiceState.Running) {
                lapTime = System.currentTimeMillis() - timeStarted

                TrackingService.timeRunInMillis.postValue(timeRun + lapTime)
                if (TrackingService.timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1L)
                    lastSecondTimestamp += 1000L
                }
                delay(Constants.TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    private fun addEmptyPolyline() = TrackingService.pathPoints.value?.apply {
        add(mutableListOf())
        TrackingService.pathPoints.postValue(this)
    } ?: TrackingService.pathPoints.postValue(mutableListOf(mutableListOf()))
}