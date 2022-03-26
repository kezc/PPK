package com.put.ubi.createpassword

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.put.ubi.PPKApplication
import com.put.ubi.R
import com.put.ubi.databinding.CreatePasswordFragmentBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.launch

class CreatePasswordFragment : Fragment(R.layout.create_password_fragment) {

    private val binding by viewBinding(CreatePasswordFragmentBinding::bind)

    //    private val viewModel: CreatePasswordViewModel by viewModels()
    private lateinit var viewModel: CreatePasswordViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = CreatePasswordViewModel(
            resources,
            (requireActivity().application as PPKApplication).userPreferences
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

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
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
            }

        }
    }
}