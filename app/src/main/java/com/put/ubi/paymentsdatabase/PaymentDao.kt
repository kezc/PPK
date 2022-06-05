package com.put.ubi.paymentsdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.put.ubi.model.Payment
import com.put.ubi.model.PaymentSource


@Dao
interface PaymentDao {
    @Insert
    suspend fun insert(payment: Payment)

    @Insert
    fun insertAll(order: List<Payment>)

    @Query("DELETE FROM payment")
    fun deleteAll()

    @Query("SELECT * FROM payment")
    suspend fun getAll(): List<Payment>

    @Query("SELECT * FROM payment WHERE source = :paymentSource")
    suspend fun getAllByPaymentSource(paymentSource: PaymentSource): List<Payment>
}
