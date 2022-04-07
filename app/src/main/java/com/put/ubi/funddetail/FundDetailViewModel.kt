package com.put.ubi.funddetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.put.ubi.BankierDataProvider
import com.put.ubi.model.Fund
import com.put.ubi.model.UnitValueWithTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FundDetailViewModel(fund: Fund, bankierDataProvider: BankierDataProvider) : ViewModel() {
    private val _historicalPrices = MutableStateFlow(listOf<UnitValueWithTime>())
    val historicalPrices = _historicalPrices.asStateFlow()

    init {
        viewModelScope.launch {
            bankierDataProvider
                .getHistoricalData(fund.bankierURL)
                ?.let { _historicalPrices.value = it }
        }
    }
}