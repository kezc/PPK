package com.put.ubi.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
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
import com.github.mikephil.charting.utils.ColorTemplate
import com.put.ubi.R
import com.put.ubi.addpayment.AddPaymentType
import com.put.ubi.databinding.FragmentDashboardBinding
import com.put.ubi.extensions.getDate
import com.put.ubi.importdata.ImportFragment
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
    private var isFABOpen = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        binding.addFab.setOnClickListener {
            if (isFABOpen) {
                closeFABMenu()
            } else {
                showFABMenu()
            }
        }

        binding.retryButton.setOnClickListener { viewModel.loadData() }

        binding.addFabOwn.setOnClickListener {
            findNavController().navigate(
                DashboardFragmentDirections.actionDashboardFragmentToAddPaymentFragment(
                    AddPaymentType.Personal
                )
            )
            isFABOpen = false
        }

        binding.addFabCountry.setOnClickListener {
            findNavController().navigate(
                DashboardFragmentDirections.actionDashboardFragmentToAddPaymentFragment(
                    AddPaymentType.Country
                )
            )
            isFABOpen = false
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
                launch {
                    viewModel.fundName.collect(binding.fundName::setText)
                }
                launch {
                    viewModel.error.collect { binding.error.isVisible = it }
                }
                launch {
                    viewModel.loading.collect {
                        binding.loading.isVisible = it
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
            setDrawValues(false)
            lineWidth = 1.5f
            isHighlightEnabled = true
            setDrawHighlightIndicators(false)
            setDrawCircles(false)
        }
        val overTimeDataSet = LineDataSet(overTimeEntries, "Value").apply {
            axisDependency = YAxis.AxisDependency.RIGHT
            setColors(ColorTemplate.MATERIAL_COLORS, 1)
            color = Color.RED
            setDrawValues(false)
            lineWidth = 1.5f
            isHighlightEnabled = true
            setDrawHighlightIndicators(false)
            setDrawCircles(false)
        }
        val lineData = LineData(historicalDataSet, overTimeDataSet)
        binding.chart.apply {
            data = lineData
            xAxis.labelCount = 5
            description = null
            val textColor = ContextCompat.getColor(requireContext(), R.color.text)
            legend.textColor = textColor
            xAxis.textColor = textColor
            axisLeft.textColor = textColor
            axisRight.textColor = textColor
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return getDate(value.toLong())
                }
            }
            invalidate()
        }
    }

    private fun showFABMenu() {
        isFABOpen = true
        binding.apply {
            addFabOwn.animate().translationY(-resources.getDimension(R.dimen.standard_60)).alpha(1f)
            addFabCountry.animate().translationY(-resources.getDimension(R.dimen.standard_120)).alpha(1f)
        }
    }

    private fun closeFABMenu() {
        isFABOpen = false
        binding.apply {
            addFabOwn.animate().translationY(0f).alpha(0f)
            addFabCountry.animate().translationY(0f).alpha(0f)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.dashboard_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                findNavController().navigate(DashboardFragmentDirections.actionDashboardFragmentToSettingsFragment())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}