package com.put.ubi.biometrics

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BiometricHelper @Inject constructor(@ApplicationContext private val context: Context) {
    fun getStatus(): Status {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                Status.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Status.NONE_ENROLLED
            }
            else -> Status.UNAVAILABLE
        }
    }

    enum class Status {
        AVAILABLE, UNAVAILABLE, NONE_ENROLLED
    }
}