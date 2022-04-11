package com.example.fitnesstracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RadioButton
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fitnesstracker.databinding.FragmentSetupStatisticsBinding
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import com.example.fitnesstracker.ui.viewmodels.SetupStatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SetupStatisticsFragment : BaseFragment<FragmentSetupStatisticsBinding>() {

    private val viewModel: SetupStatisticsViewModel by viewModels()

    override fun setup(savedInstanceState: Bundle?) {
        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        binding.apply {
            radioGroup.setOnCheckedChangeListener { _, i ->
                viewModel.updateStatisticsType(
                    when (i) {
                        rbSpeed.id -> 0
                        rbDuration.id -> 1
                        rbDistance.id -> 2
                        rbCalories.id -> 3
                        else -> {
                            return@setOnCheckedChangeListener
                        }
                    }
                )
            }
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.statisticsTypeState.collect { preferences ->
                    binding.apply {
                        (radioGroup[preferences.ordinal] as RadioButton).isChecked = true
                    }
                }
            }
        }
    }

    override fun setupBinding(inflater: LayoutInflater): FragmentSetupStatisticsBinding =
        FragmentSetupStatisticsBinding.inflate(inflater)
}