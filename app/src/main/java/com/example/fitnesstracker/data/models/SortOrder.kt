package com.example.fitnesstracker.data.models

enum class SortOrder {
    BY_DATE, BY_DURATION, BY_DISTANCE, BY_SPEED, BY_CALORIES;

    companion object {
        fun fromOrdinal(value: Int) = values().first { it.ordinal == value }
    }
}