package com.put.ubi.funddetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.put.ubi.UserPreferences
import com.put.ubi.data.BankierDataProvider
import com.put.ubi.model.Fund
import com.put.ubi.model.UnitValueWithTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FundDetailViewModel @AssistedInject constructor(
    bankierDataProvider: BankierDataProvider,
    private val userPreferences: UserPreferences,
    @Assisted private val fund: Fund,
) : ViewModel() {
    private val _historicalPrices = MutableStateFlow(listOf<UnitValueWithTime>())
    val historicalPrices = _historicalPrices.asStateFlow()

    init {
        viewModelScope.launch {
            bankierDataProvider
                .getHistoricalData(fund.bankierURL)
                ?.let { _historicalPrices.value = it }
        }
    }

    fun saveFund() = viewModelScope.launch {
        userPreferences.setFund(fund)
    }

    companion object {
        fun provideFactory(
            assistedFactory: FundDetailViewModelFactory,
            fund: Fund
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(fund) as T
            }
        }
    }
}

@AssistedFactory
interface FundDetailViewModelFactory {
    fun create(fund: Fund): FundDetailViewModel
}