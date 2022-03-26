package com.put.ubi.login

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.put.ubi.R
import com.put.ubi.UserPreferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val resources: Resources,
    private val userPreferences: UserPreferences,
) : ViewModel() {
    private val _success = MutableSharedFlow<Unit>()
    private val _error = MutableStateFlow("")
    val success = _success.asSharedFlow()
    val error = _error.asStateFlow()

    fun login(enteredPassword: String) = viewModelScope.launch {
        val password = userPreferences.getPassword()
        if (enteredPassword == password) {
            _success.emit(Unit)
        } else {
            _error.value = resources.getString(R.string.wrong_password)
        }
    }
}