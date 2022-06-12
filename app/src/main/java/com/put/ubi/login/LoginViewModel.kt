package com.put.ubi.login

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.put.ubi.R
import com.put.ubi.UserPreferences
import com.put.ubi.biometrics.BiometricHelper
import com.put.ubi.util.sha512
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val resources: Resources,
    private val userPreferences: UserPreferences,
    private val biometricHelper: BiometricHelper
) : ViewModel() {
    private val _success = MutableSharedFlow<Destination>()
    val success = _success.asSharedFlow()
    private val _passwordError = MutableStateFlow("")
    val passwordError = _passwordError.asStateFlow()
    val showBiometricsDialog = MutableSharedFlow<Unit>()
    val showBiometricsError = MutableSharedFlow<BiometricHelper.Status>()
    val showBiometricsButton = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            val areBiometricsEnabled = userPreferences.getBiometrics()
            showBiometricsButton.value = areBiometricsEnabled
            if (!areBiometricsEnabled) return@launch
            when (val areBiometricsAvailable = biometricHelper.getStatus()) {
                BiometricHelper.Status.AVAILABLE -> showBiometricsDialog.emit(Unit)
                else -> showBiometricsError.emit(areBiometricsAvailable)
            }
        }
    }

    fun login(enteredPassword: String) = viewModelScope.launch {
        val hash = withContext(Dispatchers.IO) {
            sha512(enteredPassword)
        }
        val password = userPreferences.getPassword()
        if (hash == password) {
            _success.emit(getDestination())
        } else {
            _passwordError.value = resources.getString(R.string.wrong_password)
        }
    }

    fun biometricsSucceed() = viewModelScope.launch {
        _success.emit(getDestination())
    }

    private suspend fun getDestination(): Destination {
        return if (userPreferences.getFund() == null) Destination.FUND
        else Destination.DASHBOARD
    }
    enum class Destination {
        FUND, DASHBOARD
    }
}