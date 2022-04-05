package com.example.fitnesstracker.ui.custom.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginTop
import com.google.android.material.bottomnavigation.BottomNavigationView

class HidingBottomNavgiationBarBehavior(context: Context?, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<View>(context, attrs) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency is BottomNavigationView
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        if (dependency is BottomNavigationView) {
            val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.bottomMargin = dependency.getBottom() - dependency.getTop() + child.top - dependency.getTranslationY().toInt() - child.marginTop
            child.requestLayout()
            return true
        }
        return super.onDependentViewChanged(parent, child, dependency)
    }
}