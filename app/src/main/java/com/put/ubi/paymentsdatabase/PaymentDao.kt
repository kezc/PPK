package com.put.ubi.paymentsdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.put.ubi.model.Payment
import com.put.ubi.model.PaymentSource
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Insert
    suspend fun insert(payment: Payment)

    @Query("SELECT * FROM payment")
    suspend fun getAll(): List<Payment>

    @Query("SELECT * FROM payment WHERE source = :paymentSource")
    suspend fun getAllByPaymentSource(paymentSource: PaymentSource): List<Payment>
}
