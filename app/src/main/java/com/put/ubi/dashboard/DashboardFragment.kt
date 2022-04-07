package com.put.ubi.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.put.ubi.BankierDataProvider
import com.put.ubi.BankierService
import com.put.ubi.R
import com.put.ubi.databinding.FragmentDashboardBinding
import com.put.ubi.extensions.getDate
import com.put.ubi.model.UnitValueWithTime
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols


class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private lateinit var viewModel: DashboardViewModel
    private val binding by viewBinding(FragmentDashboardBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = DashboardViewModel(BankierDataProvider(BankierService(), Gson()))

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.historicalPrices.collect { values ->
                        drawChart(values)
                    }
                }
                launch {
                    viewModel.ownPayments.collect { payments ->
                        binding.ownPaymentsAmount.text =
                            getString(R.string.money_with_currency, formatMoney(payments))
                    }
                }
                launch {
                    viewModel.countryPayments.collect { payments ->
                        binding.countryPaymentsAmount.text =
                            getString(R.string.money_with_currency, formatMoney(payments))
                    }
                }
                launch {
                    viewModel.employerPayments.collect { payments ->
                        binding.employerPaymentsAmount.text =
                            getString(R.string.money_with_currency, formatMoney(payments))
                    }
                }
                launch {
                    viewModel.sumPayments.collect { payments ->
                        binding.paymentsAmount.text =
                            getString(R.string.money_with_currency, formatMoney(payments))
                    }
                }
            }
        }
    }

    private fun formatMoney(payments: BigDecimal): String? {
        val decimalFormatSymbols = DecimalFormatSymbols().apply {
            decimalSeparator = ','
            groupingSeparator = ' '
        }
        return DecimalFormat("#,##0.00", decimalFormatSymbols).format(payments)
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