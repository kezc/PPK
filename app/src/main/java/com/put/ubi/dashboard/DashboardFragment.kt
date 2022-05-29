package com.put.ubi.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.put.ubi.R
import com.put.ubi.addpayment.AddPaymentType
import com.put.ubi.databinding.FragmentDashboardBinding
import com.put.ubi.extensions.getDate
import com.put.ubi.model.UnitValueWithTime
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val viewModel: DashboardViewModel by viewModels()
    private val binding by viewBinding(FragmentDashboardBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addFab.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToAddPaymentFragment(AddPaymentType.Personal))
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.chartData.collect { (historicalData, valueOverTime) ->
                        drawChart(historicalData, valueOverTime)
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
                launch {
                    viewModel.value.collect { payments ->
                        binding.balanceAmount.text =
                            getString(R.string.money_with_currency, formatMoney(payments))
                    }
                }
                launch {
                    viewModel.inflationValue.collect { payments ->
                        binding.inflationAmount.text =
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

    private fun drawChart(values: List<UnitValueWithTime>, valueOverTime: List<BigDecimal>) {
        val historicalEntries = values.map {
            Entry(it.time.toFloat(), it.value.toFloat())
        }
        val overTimeEntries = valueOverTime.zip(values).map { (value, unitValueWithTime) ->
            Entry(unitValueWithTime.time.toFloat(), value.toFloat())

        }
         val historicalDataSet = LineDataSet(historicalEntries, "Stock value").apply {
             axisDependency = YAxis.AxisDependency.LEFT
             color = Color.RED
             colors = valueOverTime.map { Color.RED }
         }
         val overTimeDataSet = LineDataSet(overTimeEntries, "Value").apply {
             axisDependency = YAxis.AxisDependency.RIGHT
         }
        val lineData = LineData(historicalDataSet, overTimeDataSet)
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