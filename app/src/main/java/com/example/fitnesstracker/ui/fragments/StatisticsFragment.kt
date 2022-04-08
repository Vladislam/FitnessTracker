package com.example.fitnesstracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.example.fitnesstracker.R
import com.example.fitnesstracker.databinding.FragmentStatisticsBinding
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import com.example.fitnesstracker.ui.viewmodels.StatisticsViewModel
import com.example.fitnesstracker.util.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : BaseFragment<FragmentStatisticsBinding>() {

    private val viewModel: StatisticsViewModel by viewModels()

    override fun setup(savedInstanceState: Bundle?) {
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.averageSpeedState.observe(viewLifecycleOwner) {
            binding.tvAverageSpeed.text = getString(R.string.speed_km_h, it)
        }
        viewModel.caloriesBurnedState.observe(viewLifecycleOwner) {
            binding.tvTotalCalories.text = it.toString()
        }
        viewModel.distanceState.observe(viewLifecycleOwner) {
            binding.tvTotalDistance.text =
                getString(R.string.distance_km, it / 1000f)
        }
        viewModel.runningTimeState.observe(viewLifecycleOwner) {
            binding.tvTotalTime.text = TrackingUtility.getFormattedStopWatchTime(it)
        }
    }

    override fun setupBinding(inflater: LayoutInflater): FragmentStatisticsBinding =
        FragmentStatisticsBinding.inflate(inflater)
}