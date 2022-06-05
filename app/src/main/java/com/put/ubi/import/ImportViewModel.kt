package com.put.ubi.import

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.put.ubi.UserPreferences
import com.put.ubi.model.AllUserData
import com.put.ubi.model.PaymentSource
import com.put.ubi.paymentsdatabase.PaymentDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val dao: PaymentDao
) : ViewModel() {
    private val _userData = MutableStateFlow<AllUserData?>(null)
    val userData = _userData.asStateFlow()

    val fund = _userData.map { it?.chosenFund?.name }
    val ownPayments = _userData.map {
        it?.payments?.filter { it.source == PaymentSource.INDIVIDUAL }?.sumOf { it.stockSize }
    }
    val companyPayments = _userData.map {
        it?.payments?.filter { it.source == PaymentSource.COMPANY }?.sumOf { it.stockSize }
    }
    val countryPayments = _userData.map {
        it?.payments?.filter { it.source == PaymentSource.COUNTRY }?.sumOf { it.stockSize }
    }

    private val _success = MutableSharedFlow<Unit>()
    val success = _success.asSharedFlow()

    fun setLoadedFile(allUserData: AllUserData) {
        _userData.value = allUserData
    }

    fun choose() {
        viewModelScope.launch {
            _userData.value?.let {
                it.chosenFund?.let { userPreferences.setFund(it) }
                dao.deleteAll()
                dao.insertAll(it.payments)
                _success.emit(Unit)
            }
        }
    }
}