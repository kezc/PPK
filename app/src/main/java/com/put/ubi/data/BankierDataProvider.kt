package com.put.ubi.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.put.ubi.model.UnitValueWithTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

class BankierService @Inject constructor() {
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun downloadSite(url: String): Document = withContext(Dispatchers.IO) {
        Jsoup.connect(url).get()
    }
}

@Singleton
class BankierDataProvider @Inject constructor(
    private val bankierService: BankierService,
    private val gson: Gson
) {
    suspend fun getCurrentValue(url: String): BigDecimal? {
        val regex = "[0-9]+,[0-9]+".toRegex()

        val document = bankierService.downloadSite(url)
        val cell = document.getElementsByClass("summaryTable").first()
            ?.getElementsByTag("tbody")?.first()
            ?.getElementsByTag("tr")?.first()
            ?.getElementsByClass("textBold")?.first()
            ?.text()

        return cell
            ?.let { regex.find(it)?.value }
            ?.replaceFirst(',', '.')
            ?.toBigDecimal()
    }

    suspend fun getHistoricalData(url: String): List<UnitValueWithTime>? {
        val regex = "dane_nazwa = \\[.+]".toRegex()

        val document = bankierService.downloadSite(url)
        val data = document.getElementsByAttributeValue("id", "wykres").first()?.data()
        val matchedData = data?.let { regex.find(it)?.value }?.removePrefix("dane_nazwa = ")

        val listType = object : TypeToken<List<UnitValueWithTime>>() {}.type
        return gson.fromJson(matchedData, listType)
    }
}