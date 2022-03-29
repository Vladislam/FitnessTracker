package com.example.fitnesstracker.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.models.SortMethod
import com.example.fitnesstracker.databinding.FragmentRunBinding
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import com.example.fitnesstracker.ui.viewmodels.RunViewModel
import com.example.fitnesstracker.util.extensions.throttleFirst
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.ldralighieri.corbind.view.clicks

@AndroidEntryPoint
class RunFragment : BaseFragment(R.layout.fragment_run) {

    private var _binding: FragmentRunBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RunViewModel by viewModels()

    override fun setup(savedInstanceState: Bundle?) {
        setupFab()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_run, menu)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.preferencesFlow
                .map { it.sort }
                .collectLatest { sortMethod ->
                    when (sortMethod.ordinal) {
                        0 -> menu.findItem(R.id.action_sort_by_date).isChecked = true
                        1 -> menu.findItem(R.id.action_sort_by_duration).isChecked = true
                        2 -> menu.findItem(R.id.action_sort_by_distance).isChecked = true
                        3 -> menu.findItem(R.id.action_sort_by_avg_speed).isChecked = true
                        4 -> menu.findItem(R.id.action_sort_by_calories_burned).isChecked = true
                        else -> {}
                    }
                }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_all_completed_tasks -> {
                true
            }
            R.id.action_sort_by_avg_speed -> {
                viewModel.onSortChangeClick(SortMethod.BY_SPEED)
                true
            }
            R.id.action_sort_by_calories_burned -> {
                viewModel.onSortChangeClick(SortMethod.BY_CALORIES)
                true
            }
            R.id.action_sort_by_date -> {
                viewModel.onSortChangeClick(SortMethod.BY_DATE)
                true
            }
            R.id.action_sort_by_distance -> {
                viewModel.onSortChangeClick(SortMethod.BY_DISTANCE)
                true
            }
            R.id.action_sort_by_duration -> {
                viewModel.onSortChangeClick(SortMethod.BY_DURATION)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setupBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        _binding = FragmentRunBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}