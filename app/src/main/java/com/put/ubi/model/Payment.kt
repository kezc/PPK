package com.put.ubi.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.*

@Entity
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val value: BigDecimal,
    @ColumnInfo(name = "stock_size") val stockSize: BigDecimal,
    val source: PaymentSource,
    val date: Date,
)

enum class PaymentSource {
    INDIVIDUAL, COMPANY, COUNTRY
}
