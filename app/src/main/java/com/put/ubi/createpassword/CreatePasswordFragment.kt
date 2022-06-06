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
            Log.d("DUPA", isChecked.toString())
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    Log.d("DUPA", "DUUUPA")
                    viewModel.biometricsEnabled.collect {
                        Log.d("DUPA", "DUUUPA")
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
                        Toast.makeText(requireContext(), "aa", Toast.LENGTH_SHORT).show()
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