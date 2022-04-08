package com.example.fitnesstracker.ui.custom.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.models.RunEntity
import com.example.fitnesstracker.util.TrackingUtility
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ViewConstructor")
class CustomMarkerView(
    private val runs: List<RunEntity>,
    context: Context,
    layout: Int,
    private val barChart: BarChart,
) : MarkerView(context, layout) {

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if (e == null) return

        // no way to set data differently
        runs[e.x.toInt()].apply {
            findViewById<TextView>(R.id.textViewDate).text = SimpleDateFormat(
                "dd.MM.yy",
                Locale.getDefault()
            ).format(Date(timestamp))
            findViewById<TextView>(R.id.textViewDistance).text =
                context.getString(R.string.distance_km, distanceInMeters / 1000f)
            findViewById<TextView>(R.id.textViewDuration).text =
                TrackingUtility.getFormattedStopWatchTime(runDuration)
            findViewById<TextView>(R.id.textViewCalories).text = caloriesBurned.toString()
            findViewById<TextView>(R.id.textViewAvgSpeed).text =
                context.getString(R.string.speed_km_h, avgSpeedInKMH)
        }
        barChart.invalidate()
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }
}