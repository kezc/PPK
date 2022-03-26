package com.put.ubi.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

inline fun LifecycleOwner.launchRepeatOnStart(
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    lifecycleScope.launch {
        this@launchRepeatOnStart.repeatOnLifecycle(Lifecycle.State.STARTED) {
            block()
        }
    }
}
