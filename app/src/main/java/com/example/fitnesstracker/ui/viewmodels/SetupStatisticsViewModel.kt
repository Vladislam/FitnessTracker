package com.example.fitnesstracker.ui.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.managers.PreferencesManager
import com.example.fitnesstracker.ui.viewmodels.base.PreferencesViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupStatisticsViewModel @Inject constructor(
    preferences: PreferencesManager
) : PreferencesViewModel(preferences) {
    val statisticsTypeState = preferences.statisticsTypeState

    fun updateStatisticsType(statisticsType: Int) = viewModelScope.launch {
        preferences.updateStatisticsType(statisticsType)
    }
}