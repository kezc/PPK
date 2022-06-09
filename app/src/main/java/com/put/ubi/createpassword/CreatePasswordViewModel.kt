package com.put.ubi.createpassword

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.put.ubi.R
import com.put.ubi.UserPreferences
import com.put.ubi.biometrics.BiometricHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePasswordViewModel @Inject constructor(
    private val resources: Resources,
    private val userPreferences: UserPreferences,
    private val biometricHelper: BiometricHelper
) : ViewModel() {
    private var password = MutableStateFlow("")
    private var confirmPassword = MutableStateFlow("")
    private val _passwordError = MutableStateFlow("")
    private val _confirmPasswordError = MutableStateFlow("")
    private val _success = MutableSharedFlow<Unit>()
    private val _biometricsEnabled = MutableSharedFlow<Boolean>(replay = 1)
    val biometricsEnabled = _biometricsEnabled.onStart { emit(false) }
    val biometricsError = MutableSharedFlow<BiometricHelper.Status>()

    val passwordError = _passwordError.asStateFlow()
    val confirmPasswordError = _confirmPasswordError.asStateFlow()
    val success = _success.asSharedFlow()
    val canProceed = combine(password, confirmPassword) { password, confirmPassword ->
        password.length >= 4 && confirmPassword.length >= 4
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
        _passwordError.value = validatePassword(password.value)
        if (validatePassword(confirmPassword.value).isEmpty()) {
            _confirmPasswordError.value = ""
        }
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        confirmPassword.value = newConfirmPassword
        _confirmPasswordError.value = validatePassword(confirmPassword.value)
        if (validatePassword(password.value).isEmpty()) {
            _passwordError.value = ""
        }
    }

    fun proceed() {
        if (passwordError.value.isNotEmpty() || confirmPasswordError.value.isNotEmpty()) return
        if (password.value != confirmPassword.value) {
            _confirmPasswordError.value = resources.getString(R.string.password_dont_match)
            return
        }

        savePassword()
    }

    private fun savePassword() = viewModelScope.launch {
        userPreferences.setPassword(password.value)
        _success.emit(Unit)
    }

    private fun validatePassword(it: CharSequence) = if (it.isBlank()) {
        resources.getString(R.string.field_empty)
    } else {
        ""
    }

    fun setBiometrics(checked: Boolean) = viewModelScope.launch {
        val areBiometricsAvailable = biometricHelper.getStatus()
        if (areBiometricsAvailable == BiometricHelper.Status.AVAILABLE) {
            _biometricsEnabled.emit(checked)
            userPreferences.setBiometrics(checked)
        } else {
            _biometricsEnabled.emit(false)
            userPreferences.setBiometrics(false)
            biometricsError.emit(areBiometricsAvailable)
        }
    }
}