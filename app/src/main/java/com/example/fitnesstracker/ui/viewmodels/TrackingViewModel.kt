package com.example.fitnesstracker.ui.viewmodels

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.managers.PreferencesManager
import com.example.fitnesstracker.data.models.RunEntity
import com.example.fitnesstracker.data.models.ServiceState
import com.example.fitnesstracker.data.models.TrackingUIState
import com.example.fitnesstracker.data.models.UiText
import com.example.fitnesstracker.repositories.MainRepository
import com.example.fitnesstracker.services.TrackingService
import com.example.fitnesstracker.ui.viewmodels.base.PreferencesViewModel
import com.example.fitnesstracker.util.TrackingUtility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val repository: MainRepository,
    preferencesManager: PreferencesManager,
) : PreferencesViewModel(preferencesManager) {

    val uiState: LiveData<TrackingUIState> =
        Transformations.map(TrackingService.serviceState) { state ->
            when (state) {
                ServiceState.Running -> TrackingUIState(
                    UiText.StringResource(R.string.pause),
                    false
                )
                ServiceState.Paused -> TrackingUIState(
                    UiText.StringResource(R.string.resume),
                    true
                )
                ServiceState.Stopped -> TrackingUIState(
                    UiText.StringResource(R.string.start),
                    false
                )
            }
        }

    val serviceState = TrackingService.serviceState as LiveData<ServiceState>

    fun saveRun(distanceInMeters: Int, bitmap: Bitmap?, curTimeInMillis: Long) =
        viewModelScope.launch {
            repository.insertRun(
                RunEntity(
                    image = TrackingUtility.getByteArrayFromBitmap(bitmap),
                    timestamp = Calendar.getInstance().timeInMillis,
                    avgSpeedInKMH = round((distanceInMeters / 1000.0) / (curTimeInMillis / 1000.0 / 60.0 / 60.0) * 10) / 10.0,
                    distanceInMeters = distanceInMeters,
                    runDuration = curTimeInMillis,
                    caloriesBurned = ((distanceInMeters / 1000f) * preferences.preferencesFlow.first().weight).toInt(),
                )
            )
        }
}