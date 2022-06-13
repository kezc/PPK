package com.put.ubi.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.put.ubi.data.FundsRepository
import com.put.ubi.paymentsdatabase.PaymentDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val fundsRepository: FundsRepository,
    private val paymentDao: PaymentDao
) : ViewModel() {

    private val _error = MutableStateFlow(false)
    val error = _error.asStateFlow()

    private val _data = MutableStateFlow(listOf<PaymentWithPrice>())
    val data = _data.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        _loading.value = true
        val stockPricesDeferred = async(Dispatchers.IO) {
            fundsRepository.getHistoricalDataForCurrentUser()
        }
        val allPaymentsListDeferred = async(Dispatchers.IO) { paymentDao.getAll() }
        val stockPrices = stockPricesDeferred.await()
        val allPayments = allPaymentsListDeferred.await().sortedBy { -it.date.time }
        stockPrices.onSuccess { list ->
            val fixedStockPrices = list + list.last().copy(time = Date().time)
            _data.value = allPayments.map {
                val stockPrice = fixedStockPrices.last { price -> price.time < it.date.time }
                PaymentWithPrice(it, stockPrice.value)
            }
        }
            .onFailure { _error.value = true }
        _loading.value = false
    }
}