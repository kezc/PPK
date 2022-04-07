package com.put.ubi.fundslist

import androidx.lifecycle.ViewModel
import com.put.ubi.data.FundsProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FundsViewModel(fundsProvider: FundsProvider) : ViewModel() {
    val funds = MutableStateFlow(fundsProvider.getFunds()).asStateFlow()
}