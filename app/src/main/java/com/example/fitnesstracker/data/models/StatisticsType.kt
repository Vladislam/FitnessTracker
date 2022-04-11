package com.example.fitnesstracker.data.models

enum class StatisticsType {
    SPEED, DURATION, DISTANCE, CALORIES;

    companion object {
        fun fromOrdinal(value: Int) = values().first { it.ordinal == value }
    }
}