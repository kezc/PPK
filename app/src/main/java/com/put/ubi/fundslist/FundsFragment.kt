package com.put.ubi.fundslist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.put.ubi.R
import com.put.ubi.data.FundsProvider
import com.put.ubi.databinding.FragmentFundsBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FundsFragment : Fragment(R.layout.fragment_funds) {
    private val binding by viewBinding(FragmentFundsBinding::bind)
    private val viewModel: FundsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fundsListAdapter = FundsListAdapter {
            findNavController().navigate(
                FundsFragmentDirections.actionFundsFragmentToFundDetailsFragment(
                    it
                )
            )
        }

        binding.fundsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = fundsListAdapter
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.funds.collect(fundsListAdapter::submitList) }
            }
        }
    }
}