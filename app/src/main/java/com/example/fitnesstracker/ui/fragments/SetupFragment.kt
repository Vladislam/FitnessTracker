package com.example.fitnesstracker.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fitnesstracker.R
import com.example.fitnesstracker.databinding.FragmentSetupBinding
import com.example.fitnesstracker.ui.fragments.base.BaseFragment
import com.example.fitnesstracker.ui.viewmodels.SetupViewModel
import com.example.fitnesstracker.util.extensions.throttleFirst
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks

@AndroidEntryPoint
class SetupFragment : BaseFragment(R.layout.fragment_setup) {

    private var _binding: FragmentSetupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SetupViewModel by viewModels()

    override fun setup(savedInstanceState: Bundle?) {
        setupEditTexts()
        setupButtons()
    }

    private fun setupButtons() {
        binding.apply {
            btnContinue.clicks()
                .throttleFirst()
                .onEach {
                    validateContinueClick()
                }
                .launchIn(lifecycleScope)
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

    override fun setupBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        _binding = FragmentSetupBinding.inflate(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}