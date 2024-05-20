package com.mc.model.currency_convertor

data class CurrencyUiModel(
    val code: String,
    val value: String = "0.0"
)

fun CurrencyUiModel.toCurrencyInfo(): CurrencyInfo {
    return CurrencyInfo(
        code = code,
        value = value.toDouble()
    )
}