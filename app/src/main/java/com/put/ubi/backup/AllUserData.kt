package com.put.ubi.backup

import com.put.ubi.model.Payment

data class AllUserData(
    val chosenFund: String?,
    val payments: List<Payment>
)
