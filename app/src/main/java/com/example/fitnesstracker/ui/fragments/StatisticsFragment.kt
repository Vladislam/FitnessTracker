package com.example.fitnesstracker.ui.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.fitnesstracker.R
import com.example.fitnesstracker.databinding.FragmentStatisticsBinding
import com.example.fitnesstracker.ui.custom.view.CustomMarkerView
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import com.example.fitnesstracker.ui.viewmodels.StatisticsViewModel
import com.example.fitnesstracker.util.TrackingUtility
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatisticsFragment : BaseFragment<FragmentStatisticsBinding>() {

    private val viewModel: StatisticsViewModel by viewModels()

    override fun setup(savedInstanceState: Bundle?) {
        setupBarChart()
        setupObservers()
    }

    private fun setupBarChart() {
        binding.barChart.apply {
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawLabels(false)
                setDrawGridLines(false)
                textSize = 14f
            }
            description.text = getString(R.string.bar_chart_label)
            legend.isEnabled = false
        }
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.runsByDate.collect { result ->
                val allAvgSpeeds =
                    result.indices.map { index ->
                        result[index]?.avgSpeedInKMH?.toFloat()?.let { yAxis ->
                            BarEntry(index.toFloat(), yAxis)
                        } ?: return@collect
                    }
                val barDataSet =
                    BarDataSet(allAvgSpeeds, getString(R.string.bar_chart_label)).apply {
                        TypedValue().also {
                            requireContext().theme.resolveAttribute(
                                com.google.android.material.R.attr.colorSecondary,
                                it,
                                true
                            )
                            color = it.data
                        }
                        valueTextSize = 14f
                    }
                binding.barChart.apply {
                    data = BarData(barDataSet)
                    marker =
                        CustomMarkerView(
                            result,
                            requireContext(),
                            R.layout.marker_view,
                            this
                        )
                    invalidate()
                }
            }
        }
    }

    override fun setupBinding(inflater: LayoutInflater): FragmentStatisticsBinding =
        FragmentStatisticsBinding.inflate(inflater)
}