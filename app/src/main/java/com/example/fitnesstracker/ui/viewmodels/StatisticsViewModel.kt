package com.example.fitnesstracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.fitnesstracker.data.managers.PreferencesManager
import com.example.fitnesstracker.data.models.RunEntity
import com.example.fitnesstracker.data.models.StatisticsType
import com.example.fitnesstracker.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.RealmResults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    preferences: PreferencesManager,
    repository: MainRepository
) : ViewModel() {

    val averageSpeedState = repository.getTotalAverageSpeed()
    val caloriesBurnedState = repository.getTotalCaloriesBurned()
    val distanceState = repository.getTotalDistance()
    val runningTimeState = repository.getTotalRunningTime()

    private val statisticsTypeState = preferences.statisticsTypeState

    val runsStatState: Flow<Pair<List<Number>, RealmResults<RunEntity>>> =
        statisticsTypeState.combine(
            repository.getAllRunsSortedByDate()
        ) { type, runs ->
            Pair(runs.map {
                when (type) {
                    StatisticsType.SPEED -> it.avgSpeedInKMH
                    StatisticsType.DURATION -> it.runDuration
                    StatisticsType.DISTANCE -> it.distanceInMeters
                    StatisticsType.CALORIES -> it.caloriesBurned
                }
            }, runs)
        }

    fun getStatisticsType() = runBlocking { statisticsTypeState.first() }
}
