package com.example.fitnesstracker.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.fitnesstracker.data.RunDao
import com.example.fitnesstracker.data.models.RunEntity
import com.example.fitnesstracker.data.models.SortOrder
import com.example.fitnesstracker.util.extensions.asLiveData
import io.realm.RealmResults
import io.realm.kotlin.toFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val dao: RunDao
) {
    fun insertRun(run: RunEntity) = dao.insertRun(run)

    fun deleteRun(run: RunEntity) = dao.deleteRun(run)

    fun deleteAllRuns() = dao.deleteAllRuns()

    fun getAllRunsSortedByDate(): Flow<RealmResults<RunEntity>> =
        dao.getAllRunsSortedByDate().toFlow()

    private fun getAllRunsSortedByAvgSpeed(): Flow<RealmResults<RunEntity>> =
        dao.getAllRunsSortedByAvgSpeed().toFlow()

    private fun getAllRunsSortedByCaloriesBurned(): Flow<RealmResults<RunEntity>> =
        dao.getAllRunsSortedByCaloriesBurned().toFlow()

    private fun getAllRunsSortedByDistance(): Flow<RealmResults<RunEntity>> =
        dao.getAllRunsSortedByDistance().toFlow()

    private fun getAllRunsSortedByDuration(): Flow<RealmResults<RunEntity>> =
        dao.getAllRunsSortedByDuration().toFlow()

    fun getTotalAverageSpeed(): LiveData<Double> = dao.getTotalAvgSpeed().asLiveData()
        .map { it.average(RunEntity::avgSpeedInKMH.name) }

    fun getTotalCaloriesBurned(): LiveData<Long> = dao.getTotalCaloriesBurned().asLiveData()
        .map { it.sum(RunEntity::caloriesBurned.name) as Long }

    fun getTotalDistance(): LiveData<Long> = dao.getTotalDistance().asLiveData()
        .map { it.sum(RunEntity::distanceInMeters.name) as Long }

    fun getTotalRunningTime(): LiveData<Long> = dao.getTotalRunningTime().asLiveData()
        .map { it.sum(RunEntity::runDuration.name) as Long }

    fun getRuns(sortOrder: SortOrder): Flow<RealmResults<RunEntity>> {
        return when (sortOrder) {
            SortOrder.BY_DATE -> {
                getAllRunsSortedByDate()
            }
            SortOrder.BY_CALORIES -> {
                getAllRunsSortedByCaloriesBurned()
            }
            SortOrder.BY_DISTANCE -> {
                getAllRunsSortedByDistance()
            }
            SortOrder.BY_DURATION -> {
                getAllRunsSortedByDuration()
            }
            SortOrder.BY_SPEED -> {
                getAllRunsSortedByAvgSpeed()
            }
        }
    }
}