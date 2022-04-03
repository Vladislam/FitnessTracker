package com.example.fitnesstracker.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.fitnesstracker.data.models.RunEntity
import com.example.fitnesstracker.util.extensions.asLiveData
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.toFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RunDao @Inject constructor(
    private val realm: Realm,
) {

    fun insertRun(run: RunEntity) {
        realm.executeTransactionAsync {
            it.insertOrUpdate(run)
        }
    }

    fun deleteRun(run: RunEntity) {
        realm.executeTransactionAsync {
            it.where(RunEntity::class.java)
                .equalTo(RunEntity::id.name, run.id)
                .findFirst()
                ?.deleteFromRealm()
        }
    }

    fun getAllRunsSortedByDate(): Flow<RealmResults<RunEntity>> =
        realm.where(RunEntity::class.java)
            .findAllAsync()
            .sort(RunEntity::timestamp.name, Sort.DESCENDING)
            .toFlow()

    fun getAllRunsSortedByDuration(): Flow<RealmResults<RunEntity>> =
        realm.where(RunEntity::class.java)
            .findAllAsync()
            .sort(RunEntity::runDuration.name, Sort.DESCENDING)
            .toFlow()

    fun getAllRunsSortedByCaloriesBurned(): Flow<RealmResults<RunEntity>> =
        realm.where(RunEntity::class.java)
            .findAllAsync()
            .sort(RunEntity::caloriesBurned.name, Sort.DESCENDING)
            .toFlow()

    fun getAllRunsSortedByAvgSpeed(): Flow<RealmResults<RunEntity>> =
        realm.where(RunEntity::class.java)
            .findAllAsync()
            .sort(RunEntity::avgSpeedInKMH.name, Sort.DESCENDING)
            .toFlow()

    fun getAllRunsSortedByDistance(): Flow<RealmResults<RunEntity>> =
        realm.where(RunEntity::class.java)
            .findAllAsync()
            .sort(RunEntity::distanceInMeters.name, Sort.DESCENDING)
            .toFlow()

    fun getTotalRunningTime(): LiveData<Long> =
        realm.where(RunEntity::class.java).findAllAsync().asLiveData()
            .map { it.sum(RunEntity::runDuration.name) as Long }

    fun getTotalCaloriesBurned(): LiveData<Int> =
        realm.where(RunEntity::class.java).findAllAsync().asLiveData()
            .map { it.sum(RunEntity::caloriesBurned.name) as Int }

    fun getTotalDistance(): LiveData<Int> =
        realm.where(RunEntity::class.java).findAllAsync().asLiveData()
            .map { it.sum(RunEntity::distanceInMeters.name) as Int }

    fun getTotalAvgSpeed(): LiveData<Double> =
        realm.where(RunEntity::class.java).findAllAsync().asLiveData()
            .map { it.average(RunEntity::avgSpeedInKMH.name) }

    fun deleteAllRuns() {
        realm.delete(RunEntity::class.java)
    }
}