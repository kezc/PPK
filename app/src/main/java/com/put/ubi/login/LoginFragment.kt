package com.put.ubi.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.put.ubi.R
import com.put.ubi.UserPreferences
import com.put.ubi.databinding.LoginFragmentBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LoginFragment : Fragment(R.layout.login_fragment) {
    private lateinit var viewModel: LoginViewModel
    private val binding by viewBinding(LoginFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = LoginViewModel(resources, UserPreferences(requireContext()))

        binding.confirmPasswordButton.setOnClickListener {
            viewModel.login(binding.passwordEditText.text.toString())
        }

        viewModel.error.onEach(binding.passwordTextInputLayout::setError)
            .launchIn(lifecycleScope)
        viewModel.success.onEach {
            Toast.makeText(requireContext(), "aa", Toast.LENGTH_SHORT).show()
        }.launchIn(lifecycleScope)
    }
}