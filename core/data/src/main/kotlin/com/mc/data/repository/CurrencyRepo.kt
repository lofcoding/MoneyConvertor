package com.mc.data.repository

import com.mc.model.currency_convertor.ExchangeRates
import kotlinx.coroutines.flow.Flow

interface CurrencyRepo {

    fun getExchangeRates(): Flow<ExchangeRates>

    suspend fun populateLocalDataSource(baseCurrencyCode: String)
}