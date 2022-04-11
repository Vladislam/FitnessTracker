package com.example.fitnesstracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import com.example.fitnesstracker.databinding.FragmentSetupStatisticsBinding
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupStatisticsFragment : BaseFragment<FragmentSetupStatisticsBinding>() {
    override fun setup(savedInstanceState: Bundle?) {
    }

    override fun setupBinding(inflater: LayoutInflater): FragmentSetupStatisticsBinding =
        FragmentSetupStatisticsBinding.inflate(inflater)
}