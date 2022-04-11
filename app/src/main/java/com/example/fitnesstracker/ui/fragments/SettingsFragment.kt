package com.example.fitnesstracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController
import com.example.fitnesstracker.databinding.FragmentSettingsBinding
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    override fun setup(savedInstanceState: Bundle?) {
        setupButtons()
    }

    private fun setupButtons() {
        binding.apply {
            btnUpdateUser.setOnClickListener {
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToNewUserFragment())
            }
            btnChangeStatistics.setOnClickListener {
                findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToSetupStatisticsFragment())
            }
        }
    }

    override fun setupBinding(inflater: LayoutInflater): FragmentSettingsBinding =
        FragmentSettingsBinding.inflate(inflater)
}