package com.put.ubi

import com.google.gson.Gson
import com.put.ubi.model.UnitValueWithTime
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.junit.Assert
import org.junit.Test

class BankierDataProviderTest {

    @Test
    fun `getCurrentValue should return correct value`() {
        runBlocking {
            val service = mockk<BankierService>()
            coEvery { service.downloadSite(any()) }.returns(Jsoup.parse(Mocked.getBankier()))
            val provider = createProvider(service)

            val res = provider.getCurrentValue("bankier.pl/pzu")

            Assert.assertEquals(114.78F, res)
        }
    }

    @Test
    fun `getHistoricalData should return all historical data`() {
        runBlocking {
            val service = mockk<BankierService>()
            coEvery { service.downloadSite(any()) }.returns(Jsoup.parse(Mocked.getBankier()))
            val provider = createProvider(service)

            val res = provider.getHistoricalData("bankier.pl/pzu")

            Assert.assertEquals(
                listOf(
                    UnitValueWithTime(10F, 1576022400000F),
                    UnitValueWithTime(100F, 1576108800000F)
                ), res
            )
        }
    }

    private fun createProvider(service: BankierService) =
        BankierDataProvider(service, Gson())
}