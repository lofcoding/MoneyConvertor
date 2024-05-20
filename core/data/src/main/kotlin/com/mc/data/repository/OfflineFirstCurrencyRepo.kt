package com.mc.data.repository

import com.mc.data.mapper.toEntity
import com.mc.database.db.CurrencyDatabase
import com.mc.database.model.asExternalModel
import com.mc.model.currency_convertor.ExchangeRates
import com.mc.network.service.CurrencyService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import javax.inject.Inject

class OfflineFirstCurrencyRepo @Inject constructor(
    database: CurrencyDatabase,
    private val currencyService: CurrencyService
) : CurrencyRepo {

    private val dao = database.dao()

    override fun getExchangeRates(): Flow<ExchangeRates> {
        return dao.getExchangeRates().map { it.asExternalModel() }
    }

    override suspend fun populateLocalDataSource(baseCurrencyCode: String) {
        currencyService.getExchangeRates().also {
            dao.insertExchangeRates(it.toEntity(baseCurrencyCode))
        }
    }
}