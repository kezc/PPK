package com.put.ubi.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.put.ubi.UserPreferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SplashViewModel(userPreferences: UserPreferences) : ViewModel() {
    private val _destination = MutableSharedFlow<Destination>()
    val destination = _destination.asSharedFlow()

    init {
        viewModelScope.launch {
            val password = userPreferences.getPassword()
            Log.d("DUPA", "$password")
            _destination.emit(password?.let { Destination.LOGIN } ?: Destination.CREATE_PASSWORD)
            Log.d("DUPA2222", "$password")
        }
    }

    enum class Destination {
        CREATE_PASSWORD, LOGIN
    }
}