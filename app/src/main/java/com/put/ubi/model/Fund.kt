package com.put.ubi.model

import java.io.Serializable

data class Fund(
    val name: String,
    val bankierURL: String,
    val thumbnail: String
) : Serializable