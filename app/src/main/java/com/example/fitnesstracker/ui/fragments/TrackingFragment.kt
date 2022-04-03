package com.example.fitnesstracker.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.models.RunEntity
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
import com.example.fitnesstracker.util.converters.RunSnapshotConverter
import com.example.fitnesstracker.util.extensions.showSnackBarWithAction
import com.example.fitnesstracker.util.extensions.throttleFirstClicks
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : BaseFragment(R.layout.fragment_tracking) {

    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!

    private var map: GoogleMap? = null

    private val viewModel: TrackingViewModel by viewModels()

    private var menu: Menu? = null

    private var pathPoints = mutableListOf<Polyline>()
    private var curTimeInMillis = 0L

    private var weight = 80f

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
            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync {
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
                menu?.get(0)?.isVisible = true
            }
            btnFinish.throttleFirstClicks(lifecycleScope) {
                zoomToSeeTheWholeRun()
                endRunAndSave()
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
            curTimeInMillis = it
            binding.time = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis, true)
        }
    }

    private fun zoomToSeeTheWholeRun() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (position in polyline) {
                bounds.include(position)
            }
        }
        binding.mapView.apply {
            map?.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds.build(),
                    width,
                    height,
                    (height * 0.05f).toInt()
                )
            )
        }
    }

    private fun endRunAndSave() {
        map?.snapshot { bitmap ->
            var distanceInMeters = 0
            for (polyline in pathPoints) {
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val averageSpeed =
                round((distanceInMeters / 1000.0) / (curTimeInMillis / 1000.0 / 60.0 / 60.0) * 10) / 10.0
            val dateTimestamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()
            val run = RunEntity(
                image = RunSnapshotConverter.fromBitmapToByteArray(bitmap),
                timestamp = dateTimestamp,
                avgSpeedInKMH = averageSpeed,
                distanceInMeters = distanceInMeters,
                runDuration = curTimeInMillis,
                caloriesBurned = caloriesBurned,
            )
            viewModel.insertRun(run)
            showSnackBarWithAction(
                title = getString(R.string.run_was_saved),
                actionTitle = getString(R.string.dismiss),
                view = requireActivity().findViewById(R.id.rootView)
            ) {
                dismiss()
            }
            sendCommandToService(ACTION_STOP_SERVICE)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tracking, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (curTimeInMillis > 0L) {
            menu[0].isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_cancel_run) {
            showCancelRunDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCancelRunDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.cancel_run_title)
            .setMessage(R.string.cancel_run_message)
            .setIcon(android.R.drawable.ic_menu_delete)
            .setPositiveButton(R.string.yes) { _, _ ->
                sendCommandToService(ACTION_STOP_SERVICE)
                findNavController().navigate(TrackingFragmentDirections.actionTrackingFragmentToRunFragment())
            }
            .setNegativeButton(R.string.no, null)
            .create().show()
    }

    override fun setupBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        _binding = FragmentTrackingBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}