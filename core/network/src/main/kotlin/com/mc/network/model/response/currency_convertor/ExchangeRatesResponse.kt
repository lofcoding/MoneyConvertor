package com.mc.network.model.response.currency_convertor

import com.mc.model.currency_convertor.CurrencyInfo
import com.mc.model.currency_convertor.Meta
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRatesResponse(
    val meta: Meta,
    val data: Map<String, CurrencyInfo>,
): CurrencyConvertorResponse()