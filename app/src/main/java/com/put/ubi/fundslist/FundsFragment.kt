package com.put.ubi.fundslist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.put.ubi.R
import com.put.ubi.data.FundsProvider
import com.put.ubi.databinding.FragmentFundsBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.launch

class FundsFragment : Fragment(R.layout.fragment_funds) {
    private val binding by viewBinding(FragmentFundsBinding::bind)
    private lateinit var viewModel: FundsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = FundsViewModel(FundsProvider())
        val fundsListAdapter = FundsListAdapter()

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