package com.example.fitnesstracker.util.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("app:dateText")
fun TextView.formatDate(timestamp: Long) {
    text = SimpleDateFormat(
        "dd.MM.yy",
        Locale.getDefault()
    ).format(Date(timestamp))
}