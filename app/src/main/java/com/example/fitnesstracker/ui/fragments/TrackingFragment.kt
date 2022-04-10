package com.example.fitnesstracker.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.managers.GoogleMapsManager
import com.example.fitnesstracker.data.models.RunEntity
import com.example.fitnesstracker.data.models.ServiceState
import com.example.fitnesstracker.data.models.UserPreferences
import com.example.fitnesstracker.databinding.FragmentTrackingBinding
import com.example.fitnesstracker.services.TrackingService
import com.example.fitnesstracker.ui.dialogs.CancelTrackingDialog
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import com.example.fitnesstracker.ui.viewmodels.TrackingViewModel
import com.example.fitnesstracker.util.TrackingUtility
import com.example.fitnesstracker.util.TrackingUtility.getByteArrayFromBitmap
import com.example.fitnesstracker.util.const.Constants.ACTION_PAUSE_SERVICE
import com.example.fitnesstracker.util.const.Constants.ACTION_START_SERVICE
import com.example.fitnesstracker.util.const.Constants.ACTION_STOP_SERVICE
import com.example.fitnesstracker.util.const.Constants.CANCEL_TRACKING_DIALOG_TAG
import com.example.fitnesstracker.util.extensions.showSnackBarWithAction
import com.example.fitnesstracker.util.extensions.throttleFirstClicks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : BaseFragment<FragmentTrackingBinding>() {

    private val viewModel: TrackingViewModel by viewModels()

    @Inject
    lateinit var mapManager: GoogleMapsManager

    private var menu: Menu? = null

    private var curTimeInMillis = 0L

    private lateinit var preferences: UserPreferences

    override fun setup(savedInstanceState: Bundle?) {
        restoreDialogState(savedInstanceState)
        setupMapView(savedInstanceState)
        setupButtonsCallbacks()
        setupObservers()
    }

    private fun restoreDialogState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            (parentFragmentManager.findFragmentByTag(CANCEL_TRACKING_DIALOG_TAG) as CancelTrackingDialog?)?.setPositiveClickListener { cancelRun() }
        }
    }

    private fun setupMapView(savedInstanceState: Bundle?) {
        binding.apply {
            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync {
                mapManager.map = it
                mapManager.drawAllPolylines()
            }
        }
    }

    private fun setupButtonsCallbacks() {
        binding.apply {
            btnToggleRun.throttleFirstClicks(lifecycleScope) {
                when (state?.buttonName) {
                    getString(R.string.start) -> {
                        sendCommandToService(ACTION_START_SERVICE)
                        mapManager.moveCameraToUser()
                    }
                    getString(R.string.pause) -> {
                        sendCommandToService(ACTION_PAUSE_SERVICE)
                    }
                }
            }
            btnFinish.throttleFirstClicks(lifecycleScope) {
                mapManager.zoomToSeeTheWholeRun(binding.mapView.width, binding.mapView.height)
                endRunAndSave()
            }
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.preferencesState.collect {
                preferences = it
            }
        }
        viewModel.uiState.observe(viewLifecycleOwner) {
            binding.state = it
        }
        viewModel.serviceState.observe(viewLifecycleOwner) {
            if (it is ServiceState.Running || it is ServiceState.Paused) {
                menu?.get(0)?.isVisible = true
            }
        }
        TrackingService.pathPoints.observe(viewLifecycleOwner) {
            mapManager.apply {
                pathPoints = it
                drawLatestPolyline()
                moveCameraToUser()
            }
        }
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner) {
            curTimeInMillis = it
            binding.time = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis, true)
        }
    }


    private fun endRunAndSave() {
        mapManager.map?.snapshot { bitmap ->
            val distanceInMeters = mapManager.distanceInMeters()

            val run = RunEntity(
                image = getByteArrayFromBitmap(bitmap),
                timestamp = Calendar.getInstance().timeInMillis,
                avgSpeedInKMH = round((distanceInMeters / 1000.0) / (curTimeInMillis / 1000.0 / 60.0 / 60.0) * 10) / 10.0,
                distanceInMeters = distanceInMeters,
                runDuration = curTimeInMillis,
                caloriesBurned = ((distanceInMeters / 1000f) * preferences.weight).toInt(),
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
        CancelTrackingDialog {
            cancelRun()
        }.show(parentFragmentManager, CANCEL_TRACKING_DIALOG_TAG)
    }

    private fun cancelRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.runFragment)
    }

    override fun setupBinding(inflater: LayoutInflater): FragmentTrackingBinding {
        setHasOptionsMenu(true)
        return FragmentTrackingBinding.inflate(inflater)
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