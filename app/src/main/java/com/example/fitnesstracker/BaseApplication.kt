package com.example.fitnesstracker

import android.app.Application
import com.example.fitnesstracker.data.migrations.UpdateRealmMigration
import com.example.fitnesstracker.data.migrations.UpdateRealmMigration.Companion.SCHEMA_VERSION
import dagger.hilt.android.HiltAndroidApp
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        Realm.init(this)

        Realm.setDefaultConfiguration(
            RealmConfiguration.Builder()
                .schemaVersion(SCHEMA_VERSION)
                .migration(UpdateRealmMigration())
                .build()
        )
    }
}