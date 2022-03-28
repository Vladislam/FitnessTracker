package com.example.fitnesstracker.data.migrations

import io.realm.DynamicRealm
import io.realm.RealmMigration

class UpdateRealmMigration : RealmMigration {

    companion object {
        const val SCHEMA_VERSION = 0L
    }

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {

    }
}