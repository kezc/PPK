package com.put.ubi.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.put.ubi.data.FundsRepository
import com.put.ubi.model.PaymentSource
import com.put.ubi.model.UnitValueWithTime
import com.put.ubi.paymentsdatabase.PaymentDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    fundsRepository: FundsRepository,
    paymentDao: PaymentDao
) : ViewModel() {

    private val _historicalPrices = MutableStateFlow(listOf<UnitValueWithTime>())
    val historicalPrices = _historicalPrices.asStateFlow()

    private val _ownPayments = MutableStateFlow<BigDecimal>(BigDecimal.ZERO)
    val ownPayments = _ownPayments.asStateFlow()

    private val _countryPayments = MutableStateFlow<BigDecimal>(BigDecimal.ZERO)
    val countryPayments = _countryPayments.asStateFlow()

    private val _employerPayments = MutableStateFlow<BigDecimal>(BigDecimal.ZERO)
    val employerPayments = _employerPayments.asStateFlow()

    private val _inflationValue = MutableStateFlow<BigDecimal>(BigDecimal.ZERO)
    val inflationValue = _inflationValue.asStateFlow()

    val sumPayments =
        combine(ownPayments, countryPayments, employerPayments) { x, y, z -> x + y + z }
            .stateIn(viewModelScope, SharingStarted.Eagerly, BigDecimal.ZERO)

    private val stockSize = MutableStateFlow(BigDecimal.ZERO)

    val value = combine(stockSize, historicalPrices) { stockSize, historicalPrices ->
        (historicalPrices.lastOrNull()?.value ?: BigDecimal.ZERO) * stockSize
    }.stateIn(viewModelScope, SharingStarted.Eagerly, BigDecimal.ZERO)

    init {
        viewModelScope.launch {
            val dataDeferred = async(Dispatchers.IO) {
                fundsRepository.getHistoricalDataForCurrentUser()
            }
            val ownPaymentsListDeferred =
                async(Dispatchers.IO) { paymentDao.getAllByPaymentSource(PaymentSource.INDIVIDUAL) }
            val countryPaymentsListDeferred =
                async(Dispatchers.IO) { paymentDao.getAllByPaymentSource(PaymentSource.COUNTRY) }
            val companyPaymentsListDeferred =
                async(Dispatchers.IO) { paymentDao.getAllByPaymentSource(PaymentSource.COMPANY) }

            val data = dataDeferred.await()
            val ownPayments = ownPaymentsListDeferred.await()
            val countryPayments = countryPaymentsListDeferred.await()
            val employerPayments = companyPaymentsListDeferred.await()

            _ownPayments.value = ownPayments.sumOf { it.value }
            _countryPayments.value = countryPayments.sumOf { it.value }
            _employerPayments.value = employerPayments.sumOf { it.value }

            stockSize.value = ownPayments.sumOf { it.stockSize } +
                    countryPayments.sumOf { it.stockSize } +
                    employerPayments.sumOf { it.stockSize }

            data.onSuccess { _historicalPrices.value = it }
                .onFailure { /* TODO - HANDLE FAILURE */ }

        }
    }
}