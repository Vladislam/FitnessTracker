package com.example.fitnesstracker.ui.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.managers.PreferencesManager
import com.example.fitnesstracker.data.models.SortOrder
import com.example.fitnesstracker.repositories.MainRepository
import com.example.fitnesstracker.ui.viewmodels.base.PreferencesViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunViewModel @Inject constructor(
    preferences: PreferencesManager,
    private val repository: MainRepository,
) : PreferencesViewModel(preferences) {
    val sortOrderState = preferences.sortOderState

    val runsState = sortOrderState.flatMapLatest {
        repository.getRuns(it)
    }

    fun onSortChangeClick(sortOrder: SortOrder) = viewModelScope.launch {
        preferences.updateSortMethod(sortOrder.ordinal)
    }

    fun deleteAllRuns() {
        repository.deleteAllRuns()
    }
}