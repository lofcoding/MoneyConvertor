package com.mc.data.repository

import Synchronizer
import com.mc.data.mapper.toEntity
import com.mc.database.db.CurrencyDatabase
import com.mc.database.model.asExternalModel
import com.mc.model.currency_convertor.ExchangeRates
import com.mc.network.service.CurrencyService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstCurrencyRepo @Inject constructor(
    database: CurrencyDatabase,
    private val currencyService: CurrencyService
) : CurrencyRepo {

    private val dao = database.dao()

    override fun getExchangeRates(): Flow<ExchangeRates> {
        return dao.getExchangeRates().map { it.asExternalModel() }
    }

    override suspend fun syncWith(synchronizer: Synchronizer) {
        synchronizer.start(
            fetchRemoteData = { currencyService.getExchangeRates() },
            deleteLocalData = { dao.clearExchangeRates() },
            updateLocalData = { dao.insertExchangeRates(it.toEntity("USD")) } // TODO: Get it from the response
        )
    }
}