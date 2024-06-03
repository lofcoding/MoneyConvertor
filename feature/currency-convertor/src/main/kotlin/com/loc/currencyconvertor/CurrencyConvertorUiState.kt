package com.loc.currencyconvertor

data class CurrencyConvertorUiState(
    val isLoading: Boolean = true,
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