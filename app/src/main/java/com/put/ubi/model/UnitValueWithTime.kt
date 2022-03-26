package com.put.ubi.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class UnitValueWithTime(
    @SerializedName("y")
    val value: BigDecimal,
    @SerializedName("x")
    val time: Long,
)
