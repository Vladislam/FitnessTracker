package com.example.fitnesstracker.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.databinding.FragmentTrackingBinding
import com.example.fitnesstracker.services.TrackingService
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import com.example.fitnesstracker.ui.viewmodels.TrackingViewModel
import com.example.fitnesstracker.util.Polyline
import com.example.fitnesstracker.util.TrackingUtility
import com.example.fitnesstracker.util.const.Constants.ACTION_PAUSE_SERVICE
import com.example.fitnesstracker.util.const.Constants.ACTION_START_SERVICE
import com.example.fitnesstracker.util.const.Constants.ACTION_STOP_SERVICE
import com.example.fitnesstracker.util.const.Constants.MAP_ZOOM
import com.example.fitnesstracker.util.const.Constants.POLYLINE_WIDTH
import com.example.fitnesstracker.util.extensions.throttleFirstClicks
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : BaseFragment(R.layout.fragment_tracking) {

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TrackingViewModel by viewModels()

    private var pathPoints = mutableListOf<Polyline>()

    private var map: GoogleMap? = null

    override fun setup(savedInstanceState: Bundle?) {
        setupMapView(savedInstanceState)
        setupCallbacks()
        setupButtonsCallbacks()
        setupObservers()
    }

    private fun setupCallbacks() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(TrackingFragmentDirections.actionTrackingFragmentToRunFragment())
        }
    }

    private fun setupMapView(savedInstanceState: Bundle?) {
        binding.apply {
            mapView?.onCreate(savedInstanceState)
            mapView?.getMapAsync {
                map = it
                drawAllPolylines()
            }
        }
    }

    private fun setupButtonsCallbacks() {
        binding.apply {
            btnToggleRun.throttleFirstClicks(lifecycleScope) {
                when (state?.buttonName) {
                    getString(R.string.start) -> {
                        sendCommandToService(ACTION_START_SERVICE)
                        moveCameraToUser()
                    }
                    getString(R.string.pause) -> {
                        sendCommandToService(ACTION_PAUSE_SERVICE)
                    }
                }
            }
            btnFinish.throttleFirstClicks(lifecycleScope) {
                sendCommandToService(ACTION_STOP_SERVICE)
            }
        }
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) {
            binding.state = it
        }
        TrackingService.pathPoints.observe(viewLifecycleOwner) {
            pathPoints = it
            drawLatestPolyline()
            moveCameraToUser()
        }
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner) {
            binding.time = TrackingUtility.getFormattedStopWatchTime(it, true)
        }
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun drawAllPolylines() {
        for (polyline in pathPoints) {
            map?.addPolyline(
                PolylineOptions()
                    .color(Color.RED)
                    .width(POLYLINE_WIDTH)
                    .addAll(polyline)
            )
        }
    }

    private fun drawLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLng = pathPoints.last().last()
            map?.addPolyline(
                PolylineOptions()
                    .color(Color.RED)
                    .width(POLYLINE_WIDTH)
                    .add(
                        preLastLatLng,
                        lastLatLng,
                    )
            )
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