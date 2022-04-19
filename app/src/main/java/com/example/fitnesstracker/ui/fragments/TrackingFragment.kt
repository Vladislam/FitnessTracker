package com.example.fitnesstracker.ui.fragments

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.managers.GoogleMapsManager
import com.example.fitnesstracker.data.managers.GpsManager
import com.example.fitnesstracker.data.models.ServiceState
import com.example.fitnesstracker.data.models.UserPreferences
import com.example.fitnesstracker.databinding.FragmentTrackingBinding
import com.example.fitnesstracker.services.TrackingService
import com.example.fitnesstracker.ui.dialogs.CancelTrackingDialog
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import com.example.fitnesstracker.ui.viewmodels.TrackingViewModel
import com.example.fitnesstracker.util.TrackingUtility
import com.example.fitnesstracker.util.broadcast_receivers.GpsLocationReceiver
import com.example.fitnesstracker.util.const.Constants.ACTION_PAUSE_SERVICE
import com.example.fitnesstracker.util.const.Constants.ACTION_START_SERVICE
import com.example.fitnesstracker.util.const.Constants.ACTION_STOP_SERVICE
import com.example.fitnesstracker.util.const.Constants.CANCEL_TRACKING_DIALOG_TAG
import com.example.fitnesstracker.util.extensions.showSnackBarWithAction
import com.example.fitnesstracker.util.extensions.throttleFirstClicks
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class TrackingFragment : BaseFragment<FragmentTrackingBinding>() {

    private val resolutionForResult =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            when (activityResult.resultCode) {
                Activity.RESULT_OK -> {
                    isGpsEnabled = true
                }
                Activity.RESULT_CANCELED -> {
                    showRationaleDialog(
                        getString(R.string.rationale_title),
                        getString(R.string.rationale_message)
                    )
                }
            }
        }

    private val gpsUtil by lazy {
        GpsManager(resolutionForResult, requireContext()) {
            isGpsEnabled = it
        }
    }

    private val viewModel: TrackingViewModel by viewModels()

    @Inject
    lateinit var mapManager: GoogleMapsManager

    @Inject
    lateinit var gpsReceiver: GpsLocationReceiver

    private var menu: Menu? = null

    private var curTimeInMillis = 0L

    private var isGpsEnabled = false

    private lateinit var preferences: UserPreferences

    override fun setup(savedInstanceState: Bundle?) {
        checkForGps()
        restoreDialogState(savedInstanceState)
        setupMapView(savedInstanceState)
        setupButtonsCallbacks()
        setupObservers()
    }

    private fun checkForGps() {
        gpsUtil.turnOnGPS()
        gpsReceiver.apply {
            registerCallback {
                gpsUtil.turnOnGPS()
            }
            register(
                requireContext(),
                IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
            )
        }
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
                mapManager.map?.setOnMapLoadedCallback {
                    if (TrackingService.serviceState.value is ServiceState.Running)
                        mapManager.zoomCameraToStreetsLevel()
                    else if (TrackingService.serviceState.value is ServiceState.Paused)
                        mapManager.moveAndZoomCameraToUser()
                    mapManager.drawAllPolylines()
                }
            }
        }
    }

    private fun setupButtonsCallbacks() {
        binding.apply {
            btnToggleRun.throttleFirstClicks(lifecycleScope) {
                when (state?.buttonName?.asString(requireContext())) {
                    getString(R.string.start), getString(R.string.resume) -> {
                        if (isGpsEnabled) {
                            sendCommandToService(ACTION_START_SERVICE)
                            mapManager.zoomCameraToStreetsLevel()
                        } else {
                            showSnackBarWithAction(
                                getString(R.string.error_gps_disabled),
                                getString(R.string.dismiss)
                            ) { dismiss() }
                        }
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
        viewModel.uiState.observe(viewLifecycleOwner) {
            binding.state = it
        }
        viewModel.serviceState.observe(viewLifecycleOwner) {
            if (it !is ServiceState.Stopped) {
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
            viewModel.saveRun(mapManager.distanceInMeters(), bitmap, curTimeInMillis)

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

    private fun showRationaleDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }.create().show()
    }

    override fun setupBinding(inflater: LayoutInflater): FragmentTrackingBinding {
        setHasOptionsMenu(true)
        return FragmentTrackingBinding.inflate(inflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        gpsReceiver.unregister(requireContext())
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