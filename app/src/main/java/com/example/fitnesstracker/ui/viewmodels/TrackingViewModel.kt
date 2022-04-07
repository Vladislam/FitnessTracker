package com.example.fitnesstracker.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.managers.PreferencesManager
import com.example.fitnesstracker.data.models.RunEntity
import com.example.fitnesstracker.data.models.ServiceState
import com.example.fitnesstracker.data.models.TrackingUIState
import com.example.fitnesstracker.repositories.MainRepository
import com.example.fitnesstracker.services.TrackingService
import com.example.fitnesstracker.ui.viewmodels.base.PreferencesViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: MainRepository,
    preferencesManager: PreferencesManager,
) : PreferencesViewModel(preferencesManager) {

    val uiState: LiveData<TrackingUIState> =
        Transformations.map(TrackingService.serviceState) { state ->
            when (state) {
                ServiceState.Running -> TrackingUIState(context.getString(R.string.pause), false)
                ServiceState.Paused -> TrackingUIState(context.getString(R.string.start), true)
                ServiceState.Stopped -> TrackingUIState(context.getString(R.string.start), false)
            }
        }

    val serviceState = TrackingService.serviceState as LiveData<ServiceState>

    fun insertRun(run: RunEntity) {
        repository.insertRun(run)
    }
}