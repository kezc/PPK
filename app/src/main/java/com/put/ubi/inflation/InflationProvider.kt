package com.put.ubi.inflation

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.put.ubi.extensions.resultOf
import java.net.URL
import javax.inject.Inject
import kotlin.math.pow

const val ADDRESS =
    "https://stat.gov.pl/download/gfx/portalinformacyjny/pl/defaultstronaopisowa/4741/1/1/miesieczne_wskazniki_cen_towarow_i_uslug_konsumpcyjnych_od_1982_roku_13-05-2022.csv"

class InflationProvider @Inject constructor() {
    fun getInflation(): Result<Array<Array<Float>>> = resultOf {
        val stream = URL(ADDRESS).readText()
        val readAll = csvReader {
            delimiter = ';'
        }.readAll(stream)
            .drop(1)
            .filter { it[2].startsWith("Analogiczny miesi") }
            .take(60) // 5 years * 12 months
            .map { listOf(it[3], it[4], it[5]) }
            .groupBy { it[0] }

        Array(5) {
            val year = (2018 + it).toString()
            var lastValue: Float? = null
            var lastMonth: Int? = null
            val yearValues = arrayOfNulls<Float>(12)
            for (data in readAll[year]!!) {
                lastMonth = data[1].toInt()
                if (data[2].isBlank()) {
                    yearValues[lastMonth - 1] = lastValue
                } else {
                    lastValue = (data[2]
                        .replace(',', '.')
                        .toFloat() / 100).pow(1f / 12)

                }
                yearValues[lastMonth - 1] = lastValue
            }
            yearValues.requireNoNulls()
        }
    }
}