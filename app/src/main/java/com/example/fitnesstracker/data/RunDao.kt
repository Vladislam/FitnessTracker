package com.example.fitnesstracker.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.fitnesstracker.data.models.RunEntity
import com.example.fitnesstracker.util.RealmLiveData
import com.example.fitnesstracker.util.extensions.asLiveData
import io.realm.Realm
import io.realm.Sort
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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

    fun getAllRunsSortedByDate(): RealmLiveData<RunEntity> =
        realm.where(RunEntity::class.java)
            .findAllAsync()
            .sort(RunEntity::timestamp.name, Sort.DESCENDING)
            .asLiveData()

    fun getAllRunsSortedByDuration(): RealmLiveData<RunEntity> =
        realm.where(RunEntity::class.java)
            .findAllAsync()
            .sort(RunEntity::runDuration.name, Sort.DESCENDING)
            .asLiveData()

    fun getAllRunsSortedByCaloriesBurned(): RealmLiveData<RunEntity> =
        realm.where(RunEntity::class.java)
            .findAllAsync()
            .sort(RunEntity::caloriesBurned.name, Sort.DESCENDING)
            .asLiveData()

    fun getAllRunsSortedByAvgSpeed(): RealmLiveData<RunEntity> =
        realm.where(RunEntity::class.java)
            .findAllAsync()
            .sort(RunEntity::avgSpeedInKMH.name, Sort.DESCENDING)
            .asLiveData()

    fun getAllRunsSortedByDistance(): RealmLiveData<RunEntity> =
        realm.where(RunEntity::class.java)
            .findAllAsync()
            .sort(RunEntity::distanceInMeters.name, Sort.DESCENDING)
            .asLiveData()

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
}