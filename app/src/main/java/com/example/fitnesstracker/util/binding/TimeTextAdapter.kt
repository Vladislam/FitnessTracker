package com.example.fitnesstracker.util.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.fitnesstracker.util.TrackingUtility

@BindingAdapter("app:timeText")
fun TextView.formatTime(millis: Long?) {
    millis?.let {
        text = TrackingUtility.getFormattedStopWatchTime(millis)
    }
}