package com.put.ubi.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.put.ubi.PPKApplication
import com.put.ubi.R
import com.put.ubi.databinding.LoginFragmentBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.login_fragment) {
    private lateinit var viewModel: LoginViewModel
    private val binding by viewBinding(LoginFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = LoginViewModel(
            resources,
            (requireActivity().application as PPKApplication).userPreferences
        )

        binding.confirmPasswordButton.setOnClickListener {
            viewModel.login(binding.passwordEditText.text.toString())
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.error.collect(binding.passwordTextInputLayout::setError) }
                launch {
                    viewModel.success.collect {
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToDashboardFragment())
                    }
                }
            }
        }
    }
}