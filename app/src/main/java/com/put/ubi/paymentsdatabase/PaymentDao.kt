package com.put.ubi.paymentsdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.put.ubi.model.Payment
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {
    @Insert
    suspend fun insert(payment: Payment)

    @Query("SELECT * FROM payment")
    fun getAll(): Flow<List<Payment>>
}
