package com.example.fitnesstracker.util.extensions

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.dismissError() {
    error = null
    isErrorEnabled = false
}

fun TextInputLayout.setError() {
    error = null
    isErrorEnabled = false
}