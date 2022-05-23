package com.put.ubi.data

import com.put.ubi.UserPreferences
import com.put.ubi.extensions.nonNull
import com.put.ubi.extensions.resultOf
import com.put.ubi.model.UnitValueWithTime
import java.math.BigDecimal
import javax.inject.Inject

class FundsRepository @Inject constructor(
    private val bankierDataProvider: BankierDataProvider,
    private val userPreferences: UserPreferences
) {
    suspend fun getCurrentValueForUrl(url: String): Result<BigDecimal> {
        return resultOf {
            bankierDataProvider.getCurrentValue(url)
        }.nonNull()
    }

    suspend fun getCurrentValueForCurrentUser(): Result<BigDecimal> {
        return resultOf {
            userPreferences.getFund()?.bankierURL?.let { bankierDataProvider.getCurrentValue(it) }
        }.nonNull()
    }

    suspend fun getHistoricalDataForUrl(url: String): Result<List<UnitValueWithTime>> {
        return resultOf {
            bankierDataProvider.getHistoricalData(url)
        }.nonNull()
    }

    suspend fun getHistoricalDataForCurrentUser(): Result<List<UnitValueWithTime>> {
        return resultOf {
            userPreferences.getFund()?.bankierURL?.let { bankierDataProvider.getHistoricalData(it) }
        }.nonNull()
    }
}
