package com.loc.currencyconvertor

import com.mc.model.currency_convertor.CurrencyUiModel

data class CurrencyConvertorUiState(
    val isLoading: Boolean = false,
    val allCurrencies: List<CurrencyUiModel> = emptyList(),
    val fromCurrency: CurrencyUiModel = CurrencyUiModel("",""),
    val toCurrency: CurrencyUiModel = CurrencyUiModel("",""),
    val indicativeExchangeRate: String = "",
    val lastUpdated: String = ""
) {
    companion object {
        val PreviewData = CurrencyConvertorUiState(
            fromCurrency = CurrencyUiModel(code = "SGD", value = "1000.00"),
            toCurrency = CurrencyUiModel(code = "USD", value = "736.70"),
            indicativeExchangeRate = "1 SGD = 0.7367 USD"
        )
    }
}