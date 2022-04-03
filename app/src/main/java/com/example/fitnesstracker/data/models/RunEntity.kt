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
) : RealmObject(), Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RunEntity) return false

        if (id != other.id) return false
        if (image != null) {
            if (other.image == null) return false
            if (!image.contentEquals(other.image)) return false
        } else if (other.image != null) return false
        if (timestamp != other.timestamp) return false
        if (avgSpeedInKMH != other.avgSpeedInKMH) return false
        if (distanceInMeters != other.distanceInMeters) return false
        if (runDuration != other.runDuration) return false
        if (caloriesBurned != other.caloriesBurned) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (image?.contentHashCode() ?: 0)
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + avgSpeedInKMH.hashCode()
        result = 31 * result + distanceInMeters
        result = 31 * result + runDuration.hashCode()
        result = 31 * result + caloriesBurned
        return result
    }
}