package com.example.fitnesstracker.ui.viewmodels

import com.example.fitnesstracker.data.managers.PreferencesManager
import com.example.fitnesstracker.ui.viewmodels.base.PreferencesViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: PreferencesManager,
) : PreferencesViewModel(preferencesManager)