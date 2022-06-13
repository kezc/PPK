package com.put.ubi.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.put.ubi.data.FundsRepository
import com.put.ubi.model.Payment
import com.put.ubi.paymentsdatabase.PaymentDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val paymentDao: PaymentDao
) : ViewModel() {

    private val _error = MutableStateFlow(false)
    val error = _error.asStateFlow()

    private val _data = MutableStateFlow(listOf<Payment>())
    val data = _data.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() = viewModelScope.launch {
        _loading.value = true
        _data.value = withContext(Dispatchers.IO) { paymentDao.getAll().sortedBy { -it.date.time } }
        _loading.value = false
    }
}