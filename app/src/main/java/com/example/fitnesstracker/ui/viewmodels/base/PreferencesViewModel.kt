package com.example.fitnesstracker.ui.viewmodels.base

import androidx.lifecycle.ViewModel
import com.example.fitnesstracker.data.managers.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

abstract class PreferencesViewModel(
    protected val preferences: PreferencesManager,
) : ViewModel() {

    val preferencesState = preferences.preferencesFlow

    fun getPreferences() = runBlocking {
        preferences.preferencesFlow.first()
    }
}