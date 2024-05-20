package com.loc.currencyconvertor

import com.mc.model.currency_convertor.CurrencyInfo

object CurrencyConvertor  {
    fun convert(
        fromCurrencyRateVsBaseCurrencyRate: Double,
        toCurrencyRateVsBaseCurrencyRate: Double,
        amount: Double
    ): Double {
        return (toCurrencyRateVsBaseCurrencyRate / fromCurrencyRateVsBaseCurrencyRate) * amount
    }
}










