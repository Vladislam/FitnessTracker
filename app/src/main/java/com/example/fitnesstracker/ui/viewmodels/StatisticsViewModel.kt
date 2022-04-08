package com.example.fitnesstracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.fitnesstracker.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    repository: MainRepository
) : ViewModel() {

    val averageSpeedState = repository.getTotalAverageSpeed()
    val caloriesBurnedState = repository.getTotalCaloriesBurned()
    val distanceState = repository.getTotalDistance()
    val runningTimeState = repository.getTotalRunningTime()
}