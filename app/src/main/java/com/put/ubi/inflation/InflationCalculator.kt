package com.put.ubi.inflation

import java.math.BigDecimal
import java.util.*

fun calculateCurrentValue(
    startValue: BigDecimal,
    inflationArray: Array<Array<Float>>,
    startDate: Date,
    endDate: Date = Date(),
): BigDecimal {
    val calendar = Calendar.getInstance().apply { timeInMillis = startDate.time }
    val startMonth = calendar.get(Calendar.MONTH)
    val startYear = calendar.get(Calendar.YEAR)

    calendar.timeInMillis = endDate.time
    val endMonth = calendar.get(Calendar.MONTH)
    val endYear = calendar.get(Calendar.YEAR)

    var newValue = startValue
    for (month in startMonth..11) {
        newValue *= inflationArray[startYear - 2018][month].toBigDecimal()
    }
    for (year in startYear + 1..endYear - 1) {
        for (month in 0..11) {
            newValue *= inflationArray[year - 2018][month].toBigDecimal()
        }
    }
    if (startYear != endYear) {
        for (month in 0..endMonth) {
            newValue *= inflationArray[endYear - 2018][month].toBigDecimal()
        }
    }
    return newValue
}