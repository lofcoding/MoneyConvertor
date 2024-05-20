package com.loc.currencyconvertor

import com.mc.model.currency_convertor.CurrencyUiModel

data class CurrencyConvertorUiState(
    val isLoading: Boolean = true,
    val allCurrencies: List<String> = emptyList(),
    val fromCurrencyInfo: CurrencyUiModel = CurrencyUiModel(""),
    val toCurrencyInfo: CurrencyUiModel = CurrencyUiModel(""),
    val indicativeExchangeRate: String = "",
) {
    companion object {
        val PreviewData = CurrencyConvertorUiState(
            fromCurrencyInfo = CurrencyUiModel(code = "SGD", value = "1000.00"),
            toCurrencyInfo = CurrencyUiModel(code = "USD", value = "736.70"),
            indicativeExchangeRate = "1 SGD = 0.7367 USD"
        )
    }
}