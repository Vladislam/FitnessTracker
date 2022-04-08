package com.example.fitnesstracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.models.UserPreferences
import com.example.fitnesstracker.databinding.FragmentSetupBinding
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import com.example.fitnesstracker.ui.viewmodels.SetupViewModel
import com.example.fitnesstracker.util.extensions.throttleFirstClicks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupFragment : BaseFragment<FragmentSetupBinding>() {

    private val viewModel: SetupViewModel by viewModels()

    private lateinit var preferences: UserPreferences

    override fun setup(savedInstanceState: Bundle?) {
        setupCallbacks()
        setupEditTexts()
        setupButtons()
    }

    private fun setupCallbacks() {
        preferences = viewModel.getPreferences()

        if (!preferences.isFirstTime && findNavController().currentDestination?.id == R.id.setupFragment)
            findNavController().navigate(SetupFragmentDirections.actionSetupFragmentToRunFragment())
    }

    private fun setupButtons() {
        binding.apply {
            btnContinue.throttleFirstClicks(lifecycleScope) {
                validateContinueClick()
            }
        }
    }

    private fun setupEditTexts() {
        binding.apply {
            etName.addTextChangedListener(
                onTextChanged = { s, _, _, _ ->
                    s?.let {
                        if (it.length <= 1) {
                            setNameFiledError(getString(R.string.error_name_must_be_appropriate))
                            tilName.isErrorEnabled = true
                        } else {
                            tilName.error = null
                            tilName.isErrorEnabled = false
                        }
                    }
                })

            etWeight.addTextChangedListener(
                onTextChanged = { s, _, _, _ ->
                    s?.let {
                        if (it.isEmpty()) {
                            setWeightFiledError(getString(R.string.error_field_must_not_be_empty))
                        } else {
                            tilWeight.error = null
                            tilWeight.isErrorEnabled = false
                        }
                    }
                })
        }
    }

    private fun validateContinueClick() = binding.apply {
        var isValid = true
        etName.text?.let {
            if (it.length <= 1) {
                setNameFiledError(getString(R.string.error_name_must_be_appropriate))
                isValid = false
            }
        }
        if (etWeight.text?.isEmpty() == true) {
            setWeightFiledError(getString(R.string.error_field_must_not_be_empty))
            isValid = false
        }
        if (isValid) {
            viewModel.saveCredentials(etName.text.toString(), etWeight.text.toString().toDouble())
            findNavController().navigate(SetupFragmentDirections.actionSetupFragmentToRunFragment())
        }
    }

    private fun setNameFiledError(cause: String) = binding.tilName.apply {
        error = cause
        isErrorEnabled = true
    }

    private fun setWeightFiledError(cause: String) = binding.tilWeight.apply {
        error = cause
        isErrorEnabled = true
    }

    override fun setupBinding(inflater: LayoutInflater): FragmentSetupBinding =
        FragmentSetupBinding.inflate(inflater)
}