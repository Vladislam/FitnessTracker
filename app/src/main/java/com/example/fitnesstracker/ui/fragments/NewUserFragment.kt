package com.example.fitnesstracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fitnesstracker.R
import com.example.fitnesstracker.data.models.UserPreferences
import com.example.fitnesstracker.databinding.FragmentNewUserBinding
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import com.example.fitnesstracker.ui.viewmodels.RegisterUserViewModel
import com.example.fitnesstracker.util.extensions.dismissError
import com.example.fitnesstracker.util.extensions.throttleFirstClicks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewUserFragment : BaseFragment<FragmentNewUserBinding>() {

    private val viewModel: RegisterUserViewModel by viewModels()

    private val args: NewUserFragmentArgs by navArgs()

    private lateinit var preferences: UserPreferences

    override fun setup(savedInstanceState: Bundle?) {
        setupCallbacks()
        setupEditTexts()
        setupButtons()
    }

    private fun setupCallbacks() {
        preferences = viewModel.getPreferences()

        if (!preferences.isFirstTime && findNavController().currentDestination?.id == R.id.setupFragment)
            findNavController().navigate(NewUserFragmentDirections.actionSetupFragmentToRunFragment())
    }

    private fun setupButtons() {
        binding.apply {
            isRegistered = args.isRegistered
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
                            binding.tilName.error = getString(R.string.error_name_must_be_longer)
                        } else {
                            tilName.dismissError()
                        }
                    }
                })

            etWeight.addTextChangedListener(
                onTextChanged = { s, _, _, _ ->
                    s?.let {
                        if (it.isEmpty()) {
                            binding.tilWeight.error = getString(R.string.error_required_field)
                        } else {
                            tilWeight.dismissError()
                        }
                    }
                })
        }
    }

    private fun validateContinueClick() = binding.apply {
        var isValid = true
        etName.text?.let {
            if (it.length <= 1) {
                binding.tilName.error = getString(R.string.error_name_must_be_longer)
                isValid = false
            }
        }
        if (etWeight.text?.isEmpty() == true) {
            binding.tilWeight.error = getString(R.string.error_required_field)
            isValid = false
        }
        if (isValid) {
            viewModel.saveCredentials(etName.text.toString(), etWeight.text.toString().toDouble())
            findNavController().navigate(NewUserFragmentDirections.actionSetupFragmentToRunFragment())
        }
    }

    override fun setupBinding(inflater: LayoutInflater): FragmentNewUserBinding =
        FragmentNewUserBinding.inflate(inflater)
}