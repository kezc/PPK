package com.put.ubi.splash

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.put.ubi.PPKApplication
import com.put.ubi.R
import com.put.ubi.splash.SplashViewModel.Destination.CREATE_PASSWORD
import com.put.ubi.splash.SplashViewModel.Destination.LOGIN
import kotlinx.coroutines.launch

class SplashFragment : Fragment(R.layout.splash_fragment) {
    private val viewModel: SplashViewModel by lazy {
        SplashViewModel((requireActivity().application as PPKApplication).userPreferences)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.destination.collect {
                        Log.d("DUPA2", it.toString())
                        findNavController().navigate(
                            when (it) {
                                LOGIN -> SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                                CREATE_PASSWORD -> SplashFragmentDirections.actionSplashFragmentToCreatePasswordFragment()
                            }
                        )
                    }
                }
            }
        }
    }
}