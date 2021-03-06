package com.put.ubi.funddetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.put.ubi.UserPreferences
import com.put.ubi.data.BankierDataProvider
import com.put.ubi.data.FundsRepository
import com.put.ubi.model.Fund
import com.put.ubi.model.UnitValueWithTime
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FundDetailViewModel @AssistedInject constructor(
    private val fundsRepository: FundsRepository,
    private val userPreferences: UserPreferences,
    @Assisted private val fund: Fund,
) : ViewModel() {
    private val _historicalPrices = MutableStateFlow(listOf<UnitValueWithTime>())
    val historicalPrices = _historicalPrices.asStateFlow()

    private val _success = MutableSharedFlow<Unit>()
    val success = _success.asSharedFlow()

    private val _error = MutableStateFlow(false)
    val error = _error.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _error.value = false
        viewModelScope.launch {
            fundsRepository
                .getHistoricalDataForUrl(fund.bankierURL)
                .onSuccess { _historicalPrices.value = it }
                .onFailure { _error.value = true }
        }
    }

    fun saveFund() = viewModelScope.launch {
        userPreferences.setFund(fund)
        _success.emit(Unit)
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