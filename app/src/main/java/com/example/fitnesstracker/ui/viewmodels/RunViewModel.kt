package com.example.fitnesstracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.managers.PreferencesManager
import com.example.fitnesstracker.data.models.SortMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunViewModel @Inject constructor(
    private val preferences: PreferencesManager
) : ViewModel() {

    val errorFlow = preferences.errorFlow

    val preferencesFlow = preferences.preferencesFlow

    fun onSortChangeClick(sortMethod: SortMethod) = viewModelScope.launch {
        preferences.updateSortMethod(sortMethod.ordinal)
    }
}