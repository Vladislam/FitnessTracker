package com.example.fitnesstracker.repositories

import androidx.lifecycle.LiveData
import com.example.fitnesstracker.data.RunDao
import com.example.fitnesstracker.data.models.RunEntity
import com.example.fitnesstracker.data.models.SortOrder
import io.realm.RealmResults
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val dao: RunDao
) {
    fun insertRun(run: RunEntity) = dao.insertRun(run)

    fun deleteRun(run: RunEntity) = dao.deleteRun(run)

    fun deleteAllRuns() = dao.deleteAllRuns()

    private fun getAllRunsSortedByDate(): Flow<RealmResults<RunEntity>> =
        dao.getAllRunsSortedByDate()

    private fun getAllRunsSortedByAvgSpeed(): Flow<RealmResults<RunEntity>> =
        dao.getAllRunsSortedByAvgSpeed()

    private fun getAllRunsSortedByCaloriesBurned(): Flow<RealmResults<RunEntity>> =
        dao.getAllRunsSortedByCaloriesBurned()

    private fun getAllRunsSortedByDistance(): Flow<RealmResults<RunEntity>> =
        dao.getAllRunsSortedByDistance()

    private fun getAllRunsSortedByDuration(): Flow<RealmResults<RunEntity>> =
        dao.getAllRunsSortedByDuration()

    fun getTotalAverageSpeed(): LiveData<Double> = dao.getTotalAvgSpeed()

    fun getTotalCaloriesBurned(): LiveData<Long> = dao.getTotalCaloriesBurned()

    fun getTotalDistance(): LiveData<Long> = dao.getTotalDistance()

    fun getTotalRunningTime(): LiveData<Long> = dao.getTotalRunningTime()

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