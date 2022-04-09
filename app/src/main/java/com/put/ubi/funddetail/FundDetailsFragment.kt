package com.put.ubi.funddetail

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.put.ubi.BankierDataProvider
import com.put.ubi.BankierService
import com.put.ubi.PPKApplication
import com.put.ubi.R
import com.put.ubi.databinding.FragmentFundDetailBinding
import com.put.ubi.extensions.getDate
import com.put.ubi.model.UnitValueWithTime
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.launch

class FundDetailsFragment : DialogFragment(R.layout.fragment_fund_detail) {
    private lateinit var viewModel: FundDetailViewModel
    private val binding by viewBinding(FragmentFundDetailBinding::bind)
    private val args by navArgs<FundDetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = FundDetailViewModel(
            (requireActivity().application as PPKApplication).userPreferences,
            args.fund,
            BankierDataProvider(BankierService(), Gson()),
        )

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        binding.fundName.text = args.fund.name
        binding.select.setOnClickListener { viewModel.saveFund(); dismiss() }
        binding.cancel.setOnClickListener { dismiss() }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.historicalPrices.collect { values ->
                        drawChart(values)
                    }
                }
            }
        }
    }


    private fun drawChart(values: List<UnitValueWithTime>) {
        val entries = values.map {
            Entry(it.time.toFloat(), it.value.toFloat())
        }
        val dataSet = LineDataSet(entries, "Label")
        val lineData = LineData(dataSet)
        binding.chart.apply {
            data = lineData
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