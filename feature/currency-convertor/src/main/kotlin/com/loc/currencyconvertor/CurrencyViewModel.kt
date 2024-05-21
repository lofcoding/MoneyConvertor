package com.loc.currencyconvertor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mc.data.repository.CurrencyRepo
import com.mc.model.currency_convertor.CurrencyInfo
import com.mc.model.currency_convertor.CurrencyUiModel
import com.mc.model.currency_convertor.ExchangeRates
import com.mc.model.currency_convertor.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/**
 * TODO: 1- Refactor this viewModel ✅
 *       2- Fix the . bug.
 *       3- Save the user's currencies in local storage.
 *       4- Implement the syncing logic between the local data source and the remove datasource.
 */

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyConvertorUiState())
    val uiState = _uiState.asStateFlow()

    private lateinit var exchangeRates: ExchangeRates

    init {
        viewModelScope.launch {
            currencyRepository.populateLocalDataSource("USD")
        }
        initUiState()
    }

    private fun initUiState() {
        currencyRepository
            .getExchangeRates()
            .retryWhen { cause, _ -> cause is IllegalStateException }
            .onEach { exchangeRates ->
                this.exchangeRates = exchangeRates
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        allCurrencies = exchangeRates.rates.keys.map { code ->
                            CurrencyUiModel(code = code, value = "")
                        }
                    )
                }
                setInitialCurrencies()
            }
            .launchIn(viewModelScope)
    }

    private fun setInitialCurrencies() {

        fun getInitialCurrencies(): Pair<CurrencyInfo, CurrencyInfo> {
            return Pair(
                CurrencyInfo("USD", exchangeRates.rates.getValue("USD")),
                CurrencyInfo("ILS", exchangeRates.rates.getValue("ILS"))
            )
        }

        val initialCurrencies = getUserCurrencies() ?: getInitialCurrencies()
        val convertResult = convert(
            fromVsBaseValue = initialCurrencies.first.value,
            toVsBaseValue = initialCurrencies.second.value,
            amount = 1.0
        )
        _uiState.update {
            it.copy(
                fromCurrency = initialCurrencies.first.toUiModel(),
                toCurrency = initialCurrencies.second.toUiModel().copy(value = convertResult)
            )
        }
    }

    private fun getUserCurrencies(): Pair<CurrencyInfo, CurrencyInfo>? {
        return null
    }

    fun onFromCurrencyChange(fromCurrency: CurrencyUiModel) {
        when (val validationResult = fromCurrency.value.safeToDouble()) {
            is StringToDoubleConversionResult.Valid -> {
                with(uiState.value) {
                    val convertResult = convert(
                        fromVsBaseValue = exchangeRates.rates.getValue(fromCurrency.code),
                        toVsBaseValue = exchangeRates.rates.getValue(toCurrency.code),
                        amount = validationResult.value
                    )
                    _uiState.update {
                        it.copy(
                            fromCurrency = fromCurrency,
                            toCurrency = toCurrency.copy(value = convertResult)
                        )
                    }
                }
            }

            StringToDoubleConversionResult.Empty -> {
                _uiState.update {
                    it.copy(
                        fromCurrency = it.fromCurrency.copy(value = ""),
                        toCurrency = it.toCurrency.copy(value = "")
                    )
                }
            }

            StringToDoubleConversionResult.Invalid -> Unit
        }
    }


    fun onToCurrencyChange(toCurrency: CurrencyUiModel) {
        when (val validationResult = toCurrency.value.safeToDouble()) {
            is StringToDoubleConversionResult.Valid -> {
                with(uiState.value) {
                    if (toCurrency.code != uiState.value.toCurrency.code) {
                        // Code change
                        val convertResult = convert(
                            fromVsBaseValue = exchangeRates.rates.getValue(fromCurrency.code),
                            toVsBaseValue = exchangeRates.rates.getValue(toCurrency.code),
                            amount = validationResult.value
                        )
                        _uiState.update {
                            it.copy(
                                toCurrency = toCurrency.copy(value = convertResult),
                            )
                        }
                    } else {
                        // Value change
                        val convertResult = convert(
                            fromVsBaseValue = exchangeRates.rates.getValue(this.toCurrency.code),
                            toVsBaseValue = exchangeRates.rates.getValue(fromCurrency.code),
                            amount = validationResult.value
                        )
                        _uiState.update {
                            it.copy(
                                fromCurrency = fromCurrency.copy(value = convertResult),
                                toCurrency = toCurrency
                            )
                        }
                    }
                }
            }

            StringToDoubleConversionResult.Empty -> {
                _uiState.update {
                    it.copy(
                        fromCurrency = it.fromCurrency.copy(value = ""),
                        toCurrency = it.toCurrency.copy(value = "")
                    )
                }
            }

            StringToDoubleConversionResult.Invalid -> Unit
        }
    }

    fun swapCurrencies() {
        _uiState.update {
            it.copy(
                fromCurrency = it.toCurrency,
                toCurrency = it.fromCurrency
            )
        }
    }

    private fun convert(
        fromVsBaseValue: Double,
        toVsBaseValue: Double,
        amount: Double
    ): String {
        return CurrencyConvertor.convert(
            fromCurrencyRateVsBaseCurrencyRate = fromVsBaseValue,
            toCurrencyRateVsBaseCurrencyRate = toVsBaseValue,
            amount = amount
        ).let {
            String.format(Locale.ENGLISH, "%.2f", it)
        }
    }

    private fun String.safeToDouble(): StringToDoubleConversionResult {
        return if (this.endsWith(".")) {
            StringToDoubleConversionResult.Valid(dropLast(length - 1).toDouble())
        } else if (isEmpty()) {
            return StringToDoubleConversionResult.Empty
        } else {
            this.toDoubleOrNull()?.let {
                StringToDoubleConversionResult.Valid(it)
            } ?: StringToDoubleConversionResult.Invalid
        }
    }
}

private sealed interface StringToDoubleConversionResult {
    data class Valid(val value: Double) : StringToDoubleConversionResult
    data object Invalid : StringToDoubleConversionResult
    data object Empty : StringToDoubleConversionResult
}