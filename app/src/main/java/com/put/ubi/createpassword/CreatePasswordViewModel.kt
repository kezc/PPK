package com.put.ubi.createpassword

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.put.ubi.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CreatePasswordViewModel(
    private val resources: Resources,
    private val userPreferences: UserPreferences,
) : ViewModel() {
    private var password = ""
    private var confirmPassword = ""
    private val _passwordError = MutableStateFlow("")
    private val _confirmPasswordError = MutableStateFlow("")
    private val _success = MutableSharedFlow<Unit>()

    val passwordError = _passwordError.asStateFlow()
    val confirmPasswordError = _confirmPasswordError.asStateFlow()
    val success = _success.asSharedFlow()
    val canProceed = combine(_passwordError, _confirmPasswordError) { password, confirmPassword ->
        password.length >= 4 && confirmPassword.length >= 4
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
        _passwordError.value = validatePassword(password)
        if (validatePassword(confirmPassword).isEmpty()) {
            _confirmPasswordError.value = ""
        }
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        confirmPassword = newConfirmPassword
        _confirmPasswordError.value = validatePassword(confirmPassword)
        if (validatePassword(password).isEmpty()) {
            _passwordError.value = ""
        }
    }

    fun proceed() {
        if (passwordError.value.isNotEmpty() || confirmPasswordError.value.isNotEmpty()) return
        if (password != confirmPassword) {
            _confirmPasswordError.value = "Passwords don't match"
            return
        }

        savePassword()
    }

    private fun savePassword() = viewModelScope.launch {
        userPreferences.setPassword(password)
        _success.emit(Unit)
    }

    private fun validatePassword(it: CharSequence) = if (it.isBlank()) {
        "Field is empty"
    } else {
        ""
    }
}