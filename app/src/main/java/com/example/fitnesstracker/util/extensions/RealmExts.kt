package com.example.fitnesstracker.util.extensions

import com.example.fitnesstracker.util.RealmLiveData
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults

fun <T : RealmModel> RealmResults<T>.asLiveData(): RealmLiveData<T> = RealmLiveData(this)

fun <T : RealmModel> T.copyEntity(): T = Realm.getDefaultInstance().copyFromRealm(this)