package com.put.ubi.paymentsdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.put.ubi.model.Payment
import com.put.ubi.util.BigDecimalTypeConverter
import com.put.ubi.util.DateTypeConverter

@Database(entities = [Payment::class], version = 1)
@TypeConverters(BigDecimalTypeConverter::class, DateTypeConverter::class)
abstract class PaymentDatabase : RoomDatabase() {
    abstract fun paymentDao(): PaymentDao
}