package com.example.fitnesstracker.util.wrappers

import com.example.fitnesstracker.data.models.StatisticsType
import com.example.fitnesstracker.util.TrackingUtility
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class CustomValueFormatter(private val valueType: StatisticsType) : ValueFormatter() {
    override fun getBarLabel(barEntry: BarEntry?): String {
        return barEntry?.let {
            when (valueType) {
                StatisticsType.DURATION -> TrackingUtility.getFormattedBarLabelTime(it.y.toLong())
                StatisticsType.DISTANCE -> String.format("%.1f", it.y / 1000f)
                StatisticsType.CALORIES -> it.y.toInt().toString()
                else -> super.getBarLabel(barEntry)
            }
        } ?: super.getBarLabel(barEntry)
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return axis?.let {
            when (valueType) {
                StatisticsType.DURATION -> TrackingUtility.getFormattedStopWatchTime(value.toLong())
                StatisticsType.DISTANCE -> "${(value.toInt() / 1000)}Km"
                StatisticsType.SPEED -> String.format("%.1fkm/h", value)
                StatisticsType.CALORIES -> "${value.toInt()}kcal"
            }
        } ?: super.getAxisLabel(value, axis)
    }
}