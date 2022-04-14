package com.example.fitnesstracker.data.models

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    class DynamicString(val value: String) : UiText()
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any
    ) : UiText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, args)
        }
    }
}
