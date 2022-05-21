package com.put.ubi.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.gson.Gson
import com.put.ubi.paymentsdatabase.PaymentDao
import com.put.ubi.paymentsdatabase.PaymentDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class UtilModule {
    @Provides
    fun provideGson(): Gson = Gson()

    @Provides
    fun providePaymentDatabase(@ApplicationContext context: Context): PaymentDatabase =
        Room.databaseBuilder(
            context,
            PaymentDatabase::class.java, "payment-database"
        ).build()

    @Provides
    fun providePaymentDao(paymentDatabase: PaymentDatabase): PaymentDao = paymentDatabase.paymentDao()

    @Provides
    fun provideResources(application: Application): Resources = application.resources
}