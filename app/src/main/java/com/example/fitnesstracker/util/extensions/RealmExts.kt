package com.example.fitnesstracker.util.extensions

import com.example.fitnesstracker.util.wrappers.RealmLiveData
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults

fun <T : RealmModel> RealmResults<T>.asLiveData(): RealmLiveData<T> = RealmLiveData(this)