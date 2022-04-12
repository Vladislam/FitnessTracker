package com.example.fitnesstracker.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.models.SortOrder
import com.example.fitnesstracker.databinding.FragmentRunBinding
import com.example.fitnesstracker.ui.adapters.RunAdapter
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import com.example.fitnesstracker.ui.viewmodels.RunViewModel
import com.example.fitnesstracker.util.wrappers.RequestPermissionContract
import com.example.fitnesstracker.util.TrackingUtility
import com.example.fitnesstracker.util.const.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.example.fitnesstracker.util.extensions.throttleFirst
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AppSettingsDialog
import ru.ldralighieri.corbind.view.clicks

@AndroidEntryPoint
class RunFragment : BaseFragment<FragmentRunBinding>() {

    private lateinit var runAdapter: RunAdapter

    private val permissionLauncher =
        RequestPermissionContract(this) { requestCode, permissionResult ->
            if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
                if (permissionResult == RequestPermissionContract.PermissionResult.DENIED || permissionResult == RequestPermissionContract.PermissionResult.PERMANENTLY_DENIED) {
                    AppSettingsDialog.Builder(this).build().show()
                }
            }
        }

    private val viewModel: RunViewModel by viewModels()

    override fun setup(savedInstanceState: Bundle?) {
        setupRecyclerView()
        setupCallbacks()
        requestPermission()
        setupFab()
    }

    private fun setupRecyclerView() = binding.apply {
        runAdapter = RunAdapter()
        rvRuns.adapter = runAdapter
    }

    private fun setupCallbacks() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.preferencesState.collect {
                    (requireActivity() as AppCompatActivity).supportActionBar?.title =
                        getString(R.string.welcome_user, it.name)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.runsState.collect {
                    runAdapter.submitList(it)
                }
            }
        }
    }

    private fun setupFab() {
        binding.apply {
            fab.clicks()
                .throttleFirst()
                .onEach {
                    findNavController().navigate(RunFragmentDirections.actionRunFragmentToTrackingFragment())
                }
                .launchIn(lifecycleScope)
        }
    }

    private fun requestPermission() {
        if (TrackingUtility.hasLocationPermissions(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissionLauncher.launch(
                REQUEST_CODE_LOCATION_PERMISSION,
                null,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        } else {
            permissionLauncher.launch(
                REQUEST_CODE_LOCATION_PERMISSION,
                null,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_run, menu)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sortOrderState.collect { sort ->
                menu.let {
                    when (sort.ordinal) {
                        0 -> it.findItem(R.id.action_sort_by_date).isChecked = true
                        1 -> it.findItem(R.id.action_sort_by_duration).isChecked = true
                        2 -> it.findItem(R.id.action_sort_by_distance).isChecked = true
                        3 -> it.findItem(R.id.action_sort_by_avg_speed).isChecked = true
                        4 -> it.findItem(R.id.action_sort_by_calories_burned).isChecked = true
                        else -> {}
                    }
                    (binding.rvRuns.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                        0,
                        0
                    )
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_all_runs -> {
                viewModel.deleteAllRuns()
                true
            }
            R.id.action_sort_by_avg_speed -> {
                viewModel.onSortChangeClick(SortOrder.BY_SPEED)
                true
            }
            R.id.action_sort_by_calories_burned -> {
                viewModel.onSortChangeClick(SortOrder.BY_CALORIES)
                true
            }
            R.id.action_sort_by_date -> {
                viewModel.onSortChangeClick(SortOrder.BY_DATE)
                true
            }
            R.id.action_sort_by_distance -> {
                viewModel.onSortChangeClick(SortOrder.BY_DISTANCE)
                true
            }
            R.id.action_sort_by_duration -> {
                viewModel.onSortChangeClick(SortOrder.BY_DURATION)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setupBinding(inflater: LayoutInflater): FragmentRunBinding {
        setHasOptionsMenu(true)
        return FragmentRunBinding.inflate(inflater)
    }
}