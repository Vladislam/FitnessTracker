package com.example.fitnesstracker.util.binding

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter

@SuppressLint("SetTextI18n")
@BindingAdapter("app:distanceToKm")
fun TextView.formatDistance(distance: Int) {
    text = "${(distance / 1000f)}Km"
}