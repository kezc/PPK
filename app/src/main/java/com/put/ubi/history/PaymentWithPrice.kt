package com.put.ubi.history

import com.put.ubi.model.Payment
import java.math.BigDecimal

data class PaymentWithPrice(
    val fund: Payment,
    val price: BigDecimal
)