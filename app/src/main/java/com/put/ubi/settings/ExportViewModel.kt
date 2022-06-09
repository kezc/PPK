package com.put.ubi.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.put.ubi.UserPreferences
import com.put.ubi.model.AllUserData
import com.put.ubi.paymentsdatabase.PaymentDao
import com.put.ubi.util.FileHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

private const val FILE_FORMAT = "dd_MM_yyyy"

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val gson: Gson,
    private val userPreferences: UserPreferences,
    private val paymentDao: PaymentDao,
    private val fileHelper: FileHelper
) : ViewModel() {
    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _success = MutableSharedFlow<File>()
    val success = _success.asSharedFlow()

    fun exportData() {
        viewModelScope.launch {
            _loading.value = true
            val chosenFund = async(Dispatchers.IO) { userPreferences.getFund() }
            val payments = async(Dispatchers.IO) { paymentDao.getAll() }
            val json = gson.toJson(AllUserData(chosenFund.await(), payments.await()))
            try {
                val formattedDate = SimpleDateFormat(FILE_FORMAT).format(Date())
                val file =
                    fileHelper.saveInExternalStorage("backup_$formattedDate.ppk", json.toByteArray())
                _success.emit(file)
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: ""
            }
            _loading.value = false
        }
    }
}