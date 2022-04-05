package com.example.fitnesstracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import com.example.fitnesstracker.databinding.FragmentStatisticsBinding
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : BaseFragment<FragmentStatisticsBinding>() {

    override fun setup(savedInstanceState: Bundle?) {
    }

    override fun setupBinding(inflater: LayoutInflater): FragmentStatisticsBinding =
        FragmentStatisticsBinding.inflate(inflater)
}