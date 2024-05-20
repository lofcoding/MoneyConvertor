package com.loc.currencyconvertor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mc.data.repository.CurrencyRepo
import com.mc.model.currency_convertor.CurrencyInfo
import com.mc.model.currency_convertor.CurrencyUiModel
import com.mc.model.currency_convertor.ExchangeRates
import com.mc.model.currency_convertor.toCurrencyInfo
import com.mc.model.currency_convertor.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

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
                        allCurrencies = exchangeRates.rates.keys.toList()
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
            fromCode = initialCurrencies.first.code,
            toVsBaseValue = initialCurrencies.second.value,
            toCode = initialCurrencies.second.code,
            amount = 1.0
        )
        _uiState.update {
            it.copy(
                fromCurrencyInfo = convertResult.first,
                toCurrencyInfo = convertResult.second
            )
        }
    }


    fun onFromCurrencyCodeChange(newCode: String) {
        //TODO: Safe conversion
        with(uiState.value) {
            exchangeRates.rates[newCode]?.let { fromVsBaseValue ->
                val convertResult = convert(
                    fromVsBaseValue = fromVsBaseValue,
                    fromCode = newCode,
                    toVsBaseValue = exchangeRates.rates.getValue(toCurrencyInfo.code),
                    toCode = toCurrencyInfo.code,
                    amount = fromCurrencyInfo.value.toDouble()
                )
                _uiState.update {
                    it.copy(
                        fromCurrencyInfo = convertResult.first,
                        toCurrencyInfo = convertResult.second
                    )
                }
            }
        }
    }

    fun onToCurrencyCodeChange(newCode: String) {
        with(uiState.value) {
            exchangeRates.rates[newCode]?.let { toVsBaseValue ->
                val convertResult = convert(
                    fromVsBaseValue = exchangeRates.rates.getValue(fromCurrencyInfo.code),
                    fromCode = fromCurrencyInfo.code,
                    toVsBaseValue = toVsBaseValue,
                    toCode = newCode,
                    amount = fromCurrencyInfo.value.toDouble()
                )
                _uiState.update {
                    it.copy(
                        fromCurrencyInfo = convertResult.first,
                        toCurrencyInfo = convertResult.second
                    )
                }
            }
        }
    }

    fun onFromCurrencyValueChange(newValue: String) {

    }

    fun onToCurrencyValueChange(newValue: String) {

    }

    fun swapCurrencies() {

    }

    private fun ExchangeRates.toUiModel(): List<CurrencyUiModel> {
        return rates.flatMap {
            listOf(
                CurrencyUiModel(
                    code = it.key,
                    value = "0.0"
                )
            )
        }
    }

    private fun convert(
        fromVsBaseValue: Double,
        fromCode: String,
        toVsBaseValue: Double,
        toCode: String,
        amount: Double
    ): Pair<CurrencyUiModel, CurrencyUiModel> {
        return Pair(
            CurrencyUiModel(fromCode, amount.toString()),
            CurrencyUiModel(
                code = toCode,
                value = CurrencyConvertor.convert(
                    fromCurrencyRateVsBaseCurrencyRate = fromVsBaseValue,
                    toCurrencyRateVsBaseCurrencyRate = toVsBaseValue,
                    amount = amount
                ).let {
                    String.format(Locale.ENGLISH, "%.3f", it)
                }
            )
        )
    }

}


private fun getUserCurrencies(): Pair<CurrencyInfo, CurrencyInfo>? {
    return null
}

