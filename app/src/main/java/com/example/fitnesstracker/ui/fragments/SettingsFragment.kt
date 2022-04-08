package com.example.fitnesstracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.fitnesstracker.R
import com.example.fitnesstracker.databinding.FragmentSettingsBinding
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import com.example.fitnesstracker.ui.viewmodels.SetupViewModel
import com.example.fitnesstracker.util.extensions.showSnackBarWithAction
import com.example.fitnesstracker.util.extensions.throttleFirstClicks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private val viewModel: SetupViewModel by viewModels()

    override fun setup(savedInstanceState: Bundle?) {
        setupEditTexts()
        setupButtons()
    }

    private fun setupButtons() {
        binding.apply {
            btnApplyChanges.throttleFirstClicks(lifecycleScope) {
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
            showSnackBarWithAction(
                getString(R.string.user_is_updated),
                getString(R.string.dismiss),
                view = requireActivity().findViewById(R.id.rootView),
                anchorView = requireActivity().findViewById(R.id.bottomNavigationView)
            ) {
                dismiss()
            }
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

    override fun setupBinding(inflater: LayoutInflater): FragmentSettingsBinding =
        FragmentSettingsBinding.inflate(inflater)
}