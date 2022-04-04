package com.example.fitnesstracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.managers.PreferencesManager
import com.example.fitnesstracker.data.models.SortOrder
import com.example.fitnesstracker.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunViewModel @Inject constructor(
    private val preferences: PreferencesManager,
    private val repository: MainRepository,
) : ViewModel() {
    val preferencesFlow = preferences.preferencesFlow

    val runsState = preferencesFlow.flatMapLatest {
        repository.getRuns(it.sort)
    }

    fun onSortChangeClick(sortOrder: SortOrder) = viewModelScope.launch {
        preferences.updateSortMethod(sortOrder.ordinal)
    }

    fun deleteAllRuns() {
        repository.deleteAllRuns()
    }
}