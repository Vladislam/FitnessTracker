package com.example.fitnesstracker.ui.viewmodels

import com.example.fitnesstracker.data.managers.PreferencesManager
import com.example.fitnesstracker.ui.viewmodels.base.PreferencesViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val preferences: PreferencesManager,
) : PreferencesViewModel(preferences) {

    fun saveCredentials(name: String, weight: Double) = CoroutineScope(Dispatchers.IO).launch {
        preferences.setupNewUser(name, weight)
    }
}