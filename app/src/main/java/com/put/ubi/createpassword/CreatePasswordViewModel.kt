package com.put.ubi.createpassword

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.put.ubi.R
import com.put.ubi.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CreatePasswordViewModel(
    private val resources: Resources,
    private val userPreferences: UserPreferences,
) : ViewModel() {
    private var password = MutableStateFlow("")
    private var confirmPassword  = MutableStateFlow("")
    private val _passwordError = MutableStateFlow("")
    private val _confirmPasswordError = MutableStateFlow("")
    private val _success = MutableSharedFlow<Unit>()

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
}