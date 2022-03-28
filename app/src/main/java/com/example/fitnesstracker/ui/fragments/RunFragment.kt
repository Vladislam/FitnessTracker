package com.example.fitnesstracker.ui.fragments

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.databinding.FragmentRunBinding
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class RunFragment : BaseFragment(R.layout.fragment_run) {

    private var _binding: FragmentRunBinding? = null
    private val binding get() = _binding!!

    override fun setup(savedInstanceState: Bundle?) {
        setupFab()
    }

    private fun setupFab() {
        binding.apply {
            fab.setOnClickListener {
                findNavController().navigate(RunFragmentDirections.actionRunFragmentToTrackingFragment())
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_run, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_all_completed_tasks -> {
                Timber.d("Delete all completed tasks")
                true
            }
            R.id.action_sort_by_avg_speed -> {
                Timber.d("by speed")
                item.isChecked = !item.isChecked
                true
            }
            R.id.action_sort_by_calories_burned -> {
                Timber.d("by calories")
                item.isChecked = !item.isChecked
                true
            }
            R.id.action_sort_by_date -> {
                Timber.d("by date")
                item.isChecked = !item.isChecked
                true
            }
            R.id.action_sort_by_distance -> {
                Timber.d("by distance")
                item.isChecked = !item.isChecked
                true
            }
            R.id.action_sort_by_duration -> {
                Timber.d("by duration")
                item.isChecked = !item.isChecked
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