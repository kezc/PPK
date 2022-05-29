package com.put.ubi.inflation

import org.junit.Assert
import org.junit.Test
import java.util.*
import kotlin.math.pow

class InflationCalculatorKtTest {
    @Test
    fun `Value with inflation in one year should be correctly calculated`() {
        val calendar = Calendar.getInstance()
        val startDate = Date(calendar.apply { set(2020, 0, 1) }.timeInMillis)
        val endDate = Date(calendar.apply { set(2020, 11, 1) }.timeInMillis)
        val result = calculateCurrentValue(
            100.toBigDecimal(),
            Array(5) { Array(12) { 1.10f.pow(1f / 12) } },
            startDate,
            endDate
        )
        Assert.assertEquals(110f, result.toFloat(), 0.01f)
    }

    @Test
    fun `Value with inflation within 2 years should be correctly calculated`() {
        val calendar = Calendar.getInstance()
        val startDate = Date(calendar.apply { set(2019, 11, 1) }.timeInMillis)
        val endDate = Date(calendar.apply { set(2020, 10, 1) }.timeInMillis)
        val result = calculateCurrentValue(
            100.toBigDecimal(),
            Array(5) { Array(12) { 1.10f.pow(1f / 12) } },
            startDate,
            endDate
        )
        Assert.assertEquals(110f, result.toFloat(), 0.01f)
    }

    @Test
    fun `Value with inflation in 2 years should be correctly calculated`() {
        val calendar = Calendar.getInstance()
        val startDate = Date(calendar.apply { set(2021, 0, 1) }.timeInMillis)
        val endDate = Date(calendar.apply { set(2022, 11, 1) }.timeInMillis)
        val result = calculateCurrentValue(
            100.toBigDecimal(),
            Array(5) { Array(12) { 1.10f.pow(1f / 12) } },
            startDate,
            endDate
        )
        Assert.assertEquals(121f, result.toFloat(), 0.01f)
    }
}