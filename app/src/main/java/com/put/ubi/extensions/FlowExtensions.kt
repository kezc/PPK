package com.put.ubi.extensions

import androidx.appcompat.widget.SwitchCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

fun SwitchCompat.checked(): Flow<Boolean> {
    return callbackFlow {
        setOnCheckedChangeListener { _, isChecked -> trySend(isChecked) }
        awaitClose { setOnClickListener(null) }
    }
}