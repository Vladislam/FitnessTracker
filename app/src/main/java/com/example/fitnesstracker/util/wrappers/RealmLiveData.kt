package com.example.fitnesstracker.util.wrappers

import androidx.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.RealmResults

class RealmLiveData<T : RealmModel>(
    private val results: RealmResults<T>
) : LiveData<RealmResults<T>>() {

    private val listener = RealmChangeListener<RealmResults<T>> { realmResults ->
        value = realmResults
    }

    override fun onActive() {
        super.onActive()
        if (results.isValid)
            results.addChangeListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        if (results.isValid)
            results.removeChangeListener(listener)
    }
}