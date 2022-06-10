package com.put.ubi.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.put.ubi.R
import com.put.ubi.splash.SplashViewModel.Destination.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.splash_fragment) {
    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.destination.collect {
                        findNavController().navigate(
                            when (it) {
                                LOGIN -> SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                                FUND -> SplashFragmentDirections.actionSplashFragmentToFundsFragment()
                                CREATE_PASSWORD -> SplashFragmentDirections.actionSplashFragmentToCreatePasswordFragment()
                            }
                        )
                    }
                }
            }
        }
    }
}