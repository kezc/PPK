package com.put.ubi.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.put.ubi.data.FundsRepository
import com.put.ubi.inflation.InflationProvider
import com.put.ubi.inflation.calculateCurrentValue
import com.put.ubi.model.Payment
import com.put.ubi.model.PaymentSource
import com.put.ubi.model.UnitValueWithTime
import com.put.ubi.paymentsdatabase.PaymentDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    fundsRepository: FundsRepository,
    inflationProvider: InflationProvider,
    paymentDao: PaymentDao
) : ViewModel() {

    private val historicalPrices = MutableStateFlow(listOf<UnitValueWithTime>())

    private val valueOverTime = MutableStateFlow(listOf<BigDecimal>())

    val chartData = combine(historicalPrices, valueOverTime) {historicalPrices, valueOverTime ->
            historicalPrices to valueOverTime
    }

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
            val inflationDeferred = async(Dispatchers.IO) {
                inflationProvider.getInflation()
            }
            val ownPaymentsListDeferred =
                async(Dispatchers.IO) { paymentDao.getAllByPaymentSource(PaymentSource.INDIVIDUAL) }
            val countryPaymentsListDeferred =
                async(Dispatchers.IO) { paymentDao.getAllByPaymentSource(PaymentSource.COUNTRY) }
            val companyPaymentsListDeferred =
                async(Dispatchers.IO) { paymentDao.getAllByPaymentSource(PaymentSource.COMPANY) }

            val ownPayments = ownPaymentsListDeferred.await()
            val countryPayments = countryPaymentsListDeferred.await()
            val employerPayments = companyPaymentsListDeferred.await()

            _ownPayments.value = ownPayments.sumOf { it.value }
            _countryPayments.value = countryPayments.sumOf { it.value }
            _employerPayments.value = employerPayments.sumOf { it.value }
            val allPayments =
                (ownPayments + countryPayments + employerPayments).sortedBy { it.date.time }

            stockSize.value = allPayments.sumOf { it.stockSize }

            val data = dataDeferred.await()
            data.onSuccess { list ->
                val list = list + list.last().copy(time = Date().time )
                historicalPrices.value = list
                valueOverTime.value = list.map { unitValueWithTime ->
                    allPayments
                        .takeWhile { it.date.time <= unitValueWithTime.time }
                        .sumOf { it.stockSize }
                        .multiply(unitValueWithTime.value)
                }
            }
                .onFailure { /* TODO - HANDLE FAILURE */ }

            val inflation = inflationDeferred.await()
            calculateInflation(inflation, allPayments)
        }
    }

    private fun calculateInflation(
        inflation: Result<Array<Array<Float>>>,
        allPayments: List<Payment>,
    ) {
        inflation.onSuccess { inflationArray ->
            _inflationValue.value = allPayments.map {
                calculateCurrentValue(it.value, inflationArray, it.date)
            }.sumOf { it }
        }.onFailure { }
    }
}