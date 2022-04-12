package com.example.fitnesstracker.ui.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.models.StatisticsType
import com.example.fitnesstracker.databinding.FragmentStatisticsBinding
import com.example.fitnesstracker.ui.custom.view.CustomMarkerView
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import com.example.fitnesstracker.ui.viewmodels.StatisticsViewModel
import com.example.fitnesstracker.util.TrackingUtility
import com.example.fitnesstracker.util.wrappers.CustomValueFormatter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class StatisticsFragment : BaseFragment<FragmentStatisticsBinding>() {

    private val viewModel: StatisticsViewModel by viewModels()

    override fun setup(savedInstanceState: Bundle?) {
        setupBarChart()
        setupObservers()
    }

    private fun setupBarChart() {
        val valueFormatter = CustomValueFormatter(viewModel.getStatisticsType())
        binding.barChart.apply {
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawLabels(false)
                setDrawGridLines(false)
                textSize = 14f
            }
            axisRight.valueFormatter = valueFormatter
            axisLeft.valueFormatter = valueFormatter
            description.text = getBarChartLabel()
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
            viewModel.runsStatState.collect { (values, runs) ->
                val allValues =
                    values.indices.map { index ->
                        BarEntry(index.toFloat(), values[index].toFloat())
                    }
                val barDataSet =
                    BarDataSet(allValues, getBarChartLabel()).apply {
                        TypedValue().also {
                            requireContext().theme.resolveAttribute(
                                com.google.android.material.R.attr.colorSecondary,
                                it,
                                true
                            )
                            color = it.data
                        }
                        valueFormatter = CustomValueFormatter(viewModel.getStatisticsType())
                        valueTextSize = 10f
                    }
                binding.barChart.apply {
                    data = BarData(barDataSet)
                    marker =
                        CustomMarkerView(
                            runs,
                            requireContext(),
                            R.layout.marker_view,
                            this
                        )
                    invalidate()
                }
            }
        }
    }

    private fun getBarChartLabel(): String = runBlocking {
        getString(
            R.string.bar_chart_label,
            when (viewModel.getStatisticsType()) {
                StatisticsType.SPEED -> getString(R.string.speed)
                StatisticsType.DURATION -> getString(R.string.duration)
                StatisticsType.DISTANCE -> getString(R.string.distance)
                StatisticsType.CALORIES -> getString(R.string.calories)
            }
        )
    }

    override fun setupBinding(inflater: LayoutInflater): FragmentStatisticsBinding =
        FragmentStatisticsBinding.inflate(inflater)
}