package com.put.ubi.login

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.put.ubi.R
import com.put.ubi.UserPreferences
import com.put.ubi.util.sha512
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
//class LoginViewModel constructor(
    private val resources: Resources,
    private val userPreferences: UserPreferences,
) : ViewModel() {
    private val _success = MutableSharedFlow<Unit>()
    private val _error = MutableStateFlow("")
    val success = _success.asSharedFlow()
    val error = _error.asStateFlow()

    fun login(enteredPassword: String) = viewModelScope.launch {
        val hash = withContext(Dispatchers.IO) {
            sha512(enteredPassword)
        }
        val password = userPreferences.getPassword()
        if (hash == password) {
            _success.emit(Unit)
        } else {
            _error.value = resources.getString(R.string.wrong_password)
        }
    }
}