package com.example.fitnesstracker.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitnesstracker.R
import com.example.fitnesstracker.services.TrackingService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    @ApplicationContext context: Context
) : ViewModel() {

    private val _toggleRunButtonText: MutableLiveData<String> = MutableLiveData(
        if (TrackingService.isServiceRunning) {
            context.getString(R.string.finish_run)
        } else {
            context.getString(R.string.start)
        }
    )
    val toggleRunButtonText: LiveData<String> = _toggleRunButtonText

    fun updateButtonText(text: String) = _toggleRunButtonText.postValue(text)
}