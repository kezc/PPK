package com.put.ubi.createpassword

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.put.ubi.R
import com.put.ubi.UserPreferences
import com.put.ubi.databinding.CreatePasswordFragmentBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CreatePasswordFragment : Fragment(R.layout.create_password_fragment) {

    private val binding by viewBinding(CreatePasswordFragmentBinding::bind)

    //    private val viewModel: CreatePasswordViewModel by viewModels()
    private lateinit var viewModel: CreatePasswordViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = CreatePasswordViewModel(
            resources,
            UserPreferences(requireContext())
        )

        binding.passwordEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.updatePassword(text.toString())
        }
        binding.confirmPasswordEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.updateConfirmPassword(text.toString())
        }
        binding.createPasswordButton.setOnClickListener {
            viewModel.proceed()
        }

        viewModel.passwordError
            .onEach(binding.passwordTextInputLayout::setError)
            .launchIn(lifecycleScope)
        viewModel.confirmPasswordError
            .onEach(binding.confirmPasswordTextInputLayout::setError)
            .launchIn(lifecycleScope)
        viewModel.canProceed
            .onEach(binding.createPasswordButton::setEnabled)
            .launchIn(lifecycleScope)
        viewModel.success.onEach {
            Toast.makeText(requireContext(), "aa", Toast.LENGTH_SHORT).show()
        }.launchIn(lifecycleScope)
    }

}