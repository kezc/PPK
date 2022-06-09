package com.put.ubi.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.put.ubi.R
import com.put.ubi.biometrics.BiometricHelper
import com.put.ubi.databinding.LoginFragmentBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.login_fragment) {
    val viewModel: LoginViewModel by viewModels()
    private val binding by viewBinding(LoginFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val executor = ContextCompat.getMainExecutor(requireContext())
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        requireActivity().applicationContext,
                        getString(R.string.login_fingeprint_error, errString), Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    viewModel.biometricsSucceed()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        requireActivity().applicationContext,
                        getString(R.string.login_fingerprint_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.login_fingerprint_title))
            .setSubtitle(getString(R.string.login_fingeprint_subtitle))
            .setNegativeButtonText(getString(R.string.login_fingerprint_password))
            .build()

        binding.confirmPasswordButton.setOnClickListener {
            viewModel.login(binding.passwordEditText.text.toString())
        }

        binding.passwordEditText.doOnTextChanged { _, _, _, _ ->
            binding.passwordEditText.error = null
        }
        binding.biometricsButton.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.passwordError.collect(binding.passwordTextInputLayout::setError) }
                launch {
                    viewModel.success.collect {
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToDashboardFragment())
                    }
                }
                launch {
                    viewModel.showBiometricsDialog.collect {
                        biometricPrompt.authenticate(promptInfo)
                    }
                }
                launch {
                    viewModel.showBiometricsButton.collect {
                        binding.biometricsButton.isVisible = it
                    }
                }
                launch {
                    viewModel.showBiometricsError.collect {
                        when (it) {
                            BiometricHelper.Status.AVAILABLE -> {}
                            BiometricHelper.Status.UNAVAILABLE -> Toast.makeText(
                                requireContext(),
                                getString(R.string.login_fingerprint_unavailable),
                                Toast.LENGTH_LONG
                            ).show()
                            BiometricHelper.Status.NONE_ENROLLED -> Toast.makeText(
                                requireContext(),
                                getString(R.string.login_fingeprint_none_enrolled),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }
}