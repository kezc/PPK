package com.put.ubi.model

import java.io.Serializable

data class Fund(
    val id: String,
    val name: String,
    val thumbnail: String
) : Serializable {
    val bankierURL: String
        get() = "https://www.bankier.pl/fundusze/notowania/$id"

}