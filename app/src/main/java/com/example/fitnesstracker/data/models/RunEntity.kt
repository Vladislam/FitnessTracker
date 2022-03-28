package com.example.fitnesstracker.data.models

import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
open class RunEntity(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var image: ByteArray? = null,
    var timestamp: Long = 0L,
    var avgSpeedInKMH: Double = .0,
    var distanceInMeters: Int = 0,
    var runDuration: Long = 0L,
    var caloriesBurned: Int = 0,
) : RealmObject(), Parcelable