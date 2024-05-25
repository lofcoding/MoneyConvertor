package com.loc.currencyconvertor.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.loc.currencyconvertor.CurrencyConvertorRoute

const val CurrencyConvertorRoute = "CurrencyConvertorRoute"
fun NavGraphBuilder.currencyConvertorScreen() {
    composable(route = CurrencyConvertorRoute) {
        CurrencyConvertorRoute()
    }
}