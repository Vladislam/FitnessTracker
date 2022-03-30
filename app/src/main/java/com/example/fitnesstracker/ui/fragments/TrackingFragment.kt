package com.example.fitnesstracker.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.fitnesstracker.R
import com.example.fitnesstracker.databinding.FragmentTrackingBinding
import com.example.fitnesstracker.services.TrackingService
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import com.example.fitnesstracker.ui.viewmodels.TrackingViewModel
import com.example.fitnesstracker.util.const.Constants.ACTION_START_SERVICE
import com.example.fitnesstracker.util.const.Constants.ACTION_STOP_SERVICE
import com.google.android.gms.maps.GoogleMap
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : BaseFragment(R.layout.fragment_tracking) {

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TrackingViewModel by viewModels()

    private var map: GoogleMap? = null

    override fun setup(savedInstanceState: Bundle?) {
        setupMapView(savedInstanceState)
        setupButtonCallbacks()
    }

    private fun setupMapView(savedInstanceState: Bundle?) {
        binding.apply {
            mapView?.onCreate(savedInstanceState)
            mapView?.getMapAsync { map = it }
        }
    }

    private fun setupButtonCallbacks() {
        binding.apply {
            viewModel.toggleRunButtonText.observe(viewLifecycleOwner) {
                buttonName = it
            }
            btnToggleRun.setOnClickListener {
                when (buttonName) {
                    getString(R.string.start) -> {
                        sendCommandToService(ACTION_START_SERVICE)
                        viewModel.updateButtonText(getString(R.string.finish_run))
                    }
                    getString(R.string.finish_run) -> {
                        sendCommandToService(ACTION_STOP_SERVICE)
                        viewModel.updateButtonText(getString(R.string.start))
                    }
                }
            }
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun setupBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        _binding = FragmentTrackingBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView?.onStart()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView?.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView?.onSaveInstanceState(outState)
    }
}