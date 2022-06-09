package com.put.ubi.funddetail

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.put.ubi.R
import com.put.ubi.databinding.FragmentFundDetailBinding
import com.put.ubi.extensions.getDate
import com.put.ubi.fundslist.FundsFragmentDirections
import com.put.ubi.model.UnitValueWithTime
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FundDetailsFragment : DialogFragment(R.layout.fragment_fund_detail) {
    @Inject
    lateinit var fundDetailViewModelFactory: FundDetailViewModelFactory
    private val viewModel: FundDetailViewModel by viewModels {
        FundDetailViewModel.provideFactory(fundDetailViewModelFactory, args.fund)
    }
    private val binding by viewBinding(FragmentFundDetailBinding::bind)
    private val args by navArgs<FundDetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        binding.fundName.text = args.fund.name
        binding.select.setOnClickListener { viewModel.saveFund() }
        binding.cancel.setOnClickListener { dismiss() }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.historicalPrices.collect { values ->
                        drawChart(values)
                    }
                }
                launch {
                    viewModel.success.collect {
                        requireParentFragment().findNavController().navigate(
                            FundDetailsFragmentDirections.actionFundDetailsFragmentToDashboardFragment()
                        )
                    }
                }
            }
        }
    }


    private fun drawChart(values: List<UnitValueWithTime>) {
        val entries = values.map {
            Entry(it.time.toFloat(), it.value.toFloat())
        }
        val dataSet = LineDataSet(entries, "Historial values").apply {
            setDrawValues(false)
            lineWidth = 1.5f
            isHighlightEnabled = true
            setDrawHighlightIndicators(false)
            setDrawCircles(false)
        }
        val lineData = LineData(dataSet)
        binding.chart.apply {
            data = lineData
            description = null
            xAxis.labelCount = 5
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return getDate(value.toLong())
                }
            }
            invalidate()
        }
    }
}