package com.put.ubi.addpayment

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import com.put.ubi.data.FundsRepository
import com.put.ubi.model.Payment
import com.put.ubi.model.PaymentSource
import com.put.ubi.model.UnitValueWithTime
import com.put.ubi.paymentsdatabase.PaymentDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.*

class AddPaymentViewModel @AssistedInject constructor(
    private val fundsRepository: FundsRepository,
    private val paymentDao: PaymentDao,
    @Assisted private val addPaymentType: AddPaymentType
) : ViewModel() {
    val date = MutableStateFlow<Date?>(null)
    val dateError = MutableStateFlow(false)

    val employeePaymentValue = MutableStateFlow<BigDecimal?>(null)
    val employeePaymentError = MutableStateFlow(false)

    val employerPaymentValue = MutableStateFlow<BigDecimal?>(null)
    val employerPaymentError = MutableStateFlow(false)

    val countryPaymentValue = MutableStateFlow<BigDecimal?>(null)
    val countryPaymentError = MutableStateFlow(false)

    val success = MutableSharedFlow<Unit>()
    val loading = MutableStateFlow(true)
    val loadingError = MutableStateFlow(false)

    private val historicalData = MutableStateFlow(listOf<UnitValueWithTime>())
    val unitValue: StateFlow<BigDecimal> = combine(date, historicalData) { date, historicalData ->
        val valueWithTime = date?.time?.let { millis ->
            historicalData.lastOrNull { millis > it.time }
        }
        valueWithTime?.value ?: BigDecimal.ZERO
    }.stateIn(viewModelScope, SharingStarted.Eagerly, BigDecimal.ZERO)

    init {
        loadUnitValues()
    }

    fun loadUnitValues() {
        viewModelScope.launch {
            loading.value = true
            loadingError.value = false
            withContext(Dispatchers.IO) {
                fundsRepository.getHistoricalDataForCurrentUser()
            }.onSuccess {
                historicalData.value = it
                loading.value = false
            }.onFailure {
                loadingError.value = true
                loading.value = false
            }
        }
    }

    fun onConfirm() {
        val date = date.value
        var validationSuccess = true
        if (date == null) {
            dateError.value = true
            validationSuccess = false
        }
        when (addPaymentType) {
            AddPaymentType.Personal -> {
                val employeePaymentValue = employeePaymentValue.value
                val employerPaymentValue = employerPaymentValue.value
                if (employeePaymentValue == null) {
                    employeePaymentError.value = true
                    validationSuccess = false
                }
                if (employerPaymentValue == null) {
                    employerPaymentError.value = true
                    validationSuccess = false
                }
                if (validationSuccess) {
                    viewModelScope.launch {
                        loading.value = true
                        val insertEmployee = async(Dispatchers.IO) {
                            paymentDao.insert(
                                Payment(
                                    value = checkNotNull(employeePaymentValue),
                                    stockSize = unitValue.value / employeePaymentValue,
                                    source = PaymentSource.INDIVIDUAL,
                                    date = checkNotNull(date)
                                )
                            )
                        }
                        val insertEmployer = async(Dispatchers.IO) {
                            paymentDao.insert(
                                Payment(
                                    value = checkNotNull(employerPaymentValue),
                                    stockSize = unitValue.value / employerPaymentValue,
                                    source = PaymentSource.COMPANY,
                                    date = checkNotNull(date)
                                )
                            )
                        }
                        awaitAll(insertEmployee, insertEmployer)
                        loading.value = false
                        success.emit(Unit)
                    }
                }
            }
            AddPaymentType.Country -> {
                val countryPaymentValue = countryPaymentValue.value
                if (countryPaymentValue == null) {
                    countryPaymentError.value = true
                    validationSuccess = false
                }
                if (validationSuccess) {
                    viewModelScope.launch {
                        loading.value = true
                        withContext(Dispatchers.IO) {
                            paymentDao.insert(
                                Payment(
                                    value = checkNotNull(countryPaymentValue),
                                    stockSize = unitValue.value / countryPaymentValue,
                                    source = PaymentSource.COUNTRY,
                                    date = checkNotNull(date)
                                )
                            )
                        }
                        loading.value = false
                        success.emit(Unit)
                    }
                }
            }
        }
    }

    fun setEmployeePaymentValue(value: String) {
        val paymentValue = value.toBigDecimalOrNull()
        if (paymentValue != null && employeePaymentValue.value?.compareTo(paymentValue) == 0) return

        employeePaymentValue.value = paymentValue
        paymentValue?.let {
            employerPaymentValue.value =
                it.multiply(BigDecimal.valueOf(3))
                    .divide(BigDecimal.valueOf(4), MathContext(2, RoundingMode.DOWN))
            employeePaymentError.value = false
            employerPaymentError.value = false
        }
    }

    fun setEmployerPaymentValue(value: String) {
        val paymentValue = value.toBigDecimalOrNull()
        if (paymentValue != null && employerPaymentValue.value?.compareTo(paymentValue) == 0) return

        employerPaymentValue.value = paymentValue
        paymentValue?.let {
            employeePaymentValue.value =
                it.multiply(BigDecimal.valueOf(4))
                    .divide(BigDecimal.valueOf(3), MathContext(2, RoundingMode.DOWN))
            employerPaymentError.value = false
            employeePaymentError.value = false
        }
    }

    fun setCountryPaymentValue(value: String) {
        countryPaymentValue.value = value.toBigDecimalOrNull()?.also {
            countryPaymentError.value = false
        }
    }

    fun setDate(year: Int, month: Int, dayOfMonth: Int) {
        dateError.value = false
        val newDate = getDate(year, month, dayOfMonth)
        date.value = newDate
    }

    private fun getDate(year: Int, month: Int, dayOfMonth: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth, 0, 0, 0)
        return Date(calendar.timeInMillis)
    }

    companion object {
        fun provideFactory(
            assistedFactory: AddPaymentViewModelFactory,
            AddPaymentType: AddPaymentType
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(AddPaymentType) as T
            }
        }
    }
}

@AssistedFactory
interface AddPaymentViewModelFactory {
    fun create(paymentAddPaymentType: AddPaymentType): AddPaymentViewModel
}