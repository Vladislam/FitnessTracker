package com.example.fitnesstracker.util.extensions

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

inline fun Fragment.showSnackBarWithAction(
    title: String,
    actionTitle: String,
    view: View = requireView(),
    anchorView: View? = null,
    crossinline action: Snackbar.() -> Unit
) =
    Snackbar.make(view, title, Snackbar.LENGTH_LONG).apply {
        setAnchorView(anchorView)
        animationMode = Snackbar.ANIMATION_MODE_FADE
        setAction(actionTitle) {
            action()
        }
    }.show()