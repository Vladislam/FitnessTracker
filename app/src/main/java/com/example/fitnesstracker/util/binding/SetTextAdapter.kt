package com.example.fitnesstracker.util.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.fitnesstracker.data.models.UiText

@BindingAdapter("app:setText")
fun TextView.setText(text: UiText) {
    this.text = text.asString(context)
}