package com.put.ubi

import com.google.gson.annotations.SerializedName

data class UnitValueWithTime(
    @SerializedName("y")
    val value: Float,
    @SerializedName("x")
    val time: Float,
)
