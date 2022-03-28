package com.example.fitnesstracker.repositories

import androidx.lifecycle.LiveData
import com.example.fitnesstracker.data.RunDao
import com.example.fitnesstracker.data.models.RunEntity
import com.example.fitnesstracker.util.RealmLiveData
import javax.inject.Inject
import javax.inject.Singleton

class MainRepository @Inject constructor(
    private val dao: RunDao
) {
    fun insertRun(run: RunEntity) = dao.insertRun(run)

    fun deleteRun(run: RunEntity) = dao.deleteRun(run)

    fun getAllRunsSortedByDate(): RealmLiveData<RunEntity> = dao.getAllRunsSortedByDate()

    fun getAllRunsSortedByAvgSpeed(): RealmLiveData<RunEntity> = dao.getAllRunsSortedByAvgSpeed()

    fun getAllRunsSortedByCaloriesBurned(): RealmLiveData<RunEntity> =
        dao.getAllRunsSortedByCaloriesBurned()

    fun getAllRunsSortedByDistance(): RealmLiveData<RunEntity> = dao.getAllRunsSortedByDistance()

    fun getAllRunsSortedByDuration(): RealmLiveData<RunEntity> = dao.getAllRunsSortedByDuration()

    fun getTotalAverageSpeed(): LiveData<Double> = dao.getTotalAvgSpeed()

    fun getTotalCaloriesBurned(): LiveData<Int> = dao.getTotalCaloriesBurned()

    fun getTotalDistance(): LiveData<Int> = dao.getTotalDistance()

    fun getTotalRunningTime(): LiveData<Long> = dao.getTotalRunningTime()
}