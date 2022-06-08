package com.put.ubi.createpassword

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.put.ubi.R
import com.put.ubi.biometrics.BiometricHelper
import com.put.ubi.databinding.CreatePasswordFragmentBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePasswordFragment : Fragment(R.layout.create_password_fragment) {

    private val binding by viewBinding(CreatePasswordFragmentBinding::bind)

    private val viewModel: CreatePasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.passwordEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.updatePassword(text.toString())
        }
        binding.confirmPasswordEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.updateConfirmPassword(text.toString())
        }
        binding.createPasswordButton.setOnClickListener {
            viewModel.proceed()
        }
        binding.enableBiometrics.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setBiometrics(isChecked)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.biometricsEnabled.collect {
                        binding.enableBiometrics.isChecked = it
                    }
                }
                launch {
                    viewModel.passwordError.collect(
                        binding.passwordTextInputLayout::setError
                    )
                }
                launch {
                    viewModel.confirmPasswordError.collect(
                        binding.confirmPasswordTextInputLayout::setError
                    )
                }
                launch {
                    viewModel.canProceed.collect(
                        binding.createPasswordButton::setEnabled
                    )
                }
                launch {
                    viewModel.success.collect {
                        findNavController().navigate(CreatePasswordFragmentDirections.actionCreatePasswordFragmentToFundsFragment())
                    }
                }
                launch {
                    viewModel.biometricsError.collect {
                        when (it) {
                            BiometricHelper.Status.AVAILABLE -> {
                            }
                            BiometricHelper.Status.UNAVAILABLE -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Fingerprint is unavailable",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            BiometricHelper.Status.NONE_ENROLLED -> {
                                Toast.makeText(
                                    requireContext(),
                                    "You need to add fingerprint in settings",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }
}