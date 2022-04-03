package com.example.fitnesstracker.util.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("app:caloriesText")
fun TextView.setCalories(kcal: Int?) {
    text = kcal.toString()
}