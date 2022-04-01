package com.example.fitnesstracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.managers.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val preferences: PreferencesManager,
) : ViewModel() {

    val preferencesFlow = preferences.preferencesFlow.asLiveData()

    fun saveCredentials(name: String, weight: Double) = viewModelScope.launch {
        preferences.updateName(name)
        preferences.updateWeight(weight)
        preferences.updateIsFirstFirstTime(false)
    }
}