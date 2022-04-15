package com.put.ubi.fundslist

import androidx.lifecycle.ViewModel
import com.put.ubi.data.FundsProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FundsViewModel @Inject constructor(fundsProvider: FundsProvider) : ViewModel() {
    val funds = MutableStateFlow(fundsProvider.getFunds()).asStateFlow()
}