package com.example.fitnesstracker.data

import com.example.fitnesstracker.data.models.RunEntity
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
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

    fun deleteAllRuns() {
        realm.executeTransactionAsync {
            it.delete(RunEntity::class.java)
        }
    }

    fun getAllRunsSortedByDate(): RealmResults<RunEntity> =
        realm.where(RunEntity::class.java)
            .findAllAsync()
            .sort(RunEntity::timestamp.name, Sort.DESCENDING)

    fun getAllRunsSortedByDuration(): RealmResults<RunEntity> =
        realm.where(RunEntity::class.java)
            .findAllAsync()
            .sort(RunEntity::runDuration.name, Sort.DESCENDING)

    fun getAllRunsSortedByCaloriesBurned(): RealmResults<RunEntity> =
        realm.where(RunEntity::class.java)
            .findAllAsync()
            .sort(RunEntity::caloriesBurned.name, Sort.DESCENDING)

    fun getAllRunsSortedByAvgSpeed(): RealmResults<RunEntity> =
        realm.where(RunEntity::class.java)
            .findAllAsync()
            .sort(RunEntity::avgSpeedInKMH.name, Sort.DESCENDING)

    fun getAllRunsSortedByDistance(): RealmResults<RunEntity> =
        realm.where(RunEntity::class.java)
            .findAllAsync()
            .sort(RunEntity::distanceInMeters.name, Sort.DESCENDING)

    fun getTotalRunningTime(): RealmResults<RunEntity> =
        realm.where(RunEntity::class.java).findAllAsync()

    fun getTotalCaloriesBurned(): RealmResults<RunEntity> =
        realm.where(RunEntity::class.java).findAllAsync()

    fun getTotalDistance(): RealmResults<RunEntity> =
        realm.where(RunEntity::class.java).findAllAsync()

    fun getTotalAvgSpeed(): RealmResults<RunEntity> =
        realm.where(RunEntity::class.java).findAllAsync()
}