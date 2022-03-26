package com.put.ubi.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.put.ubi.BankierDataProvider
import com.put.ubi.model.UnitValueWithTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.math.BigDecimal

class DashboardViewModel(
    bankierDataProvider: BankierDataProvider
) : ViewModel() {
    private val _historicalPrices = MutableStateFlow(listOf<UnitValueWithTime>())
    val historicalPrices = _historicalPrices.asStateFlow()

    private val _ownPayments = MutableStateFlow<BigDecimal>(10000.toBigDecimal())
    val ownPayments = _ownPayments.asStateFlow()

    private val _countryPayments = MutableStateFlow<BigDecimal>(100.toBigDecimal())
    val countryPayments = _countryPayments.asStateFlow()

    private val _employerPayments = MutableStateFlow<BigDecimal>(100.toBigDecimal())
    val employerPayments = _employerPayments.asStateFlow()

    private val _inflationValue = MutableStateFlow<BigDecimal>(1000.toBigDecimal())
    val inflationValue = _inflationValue.asStateFlow()

    val sumPayments = combine(ownPayments, countryPayments, employerPayments) {x, y, z -> x + y + z}

    init {
        viewModelScope.launch {
            bankierDataProvider
                .getHistoricalData("https://www.bankier.pl/fundusze/notowania/PZU55")
                ?.let { _historicalPrices.value = it }
        }
    }
}