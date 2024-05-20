package com.loc.currencyconvertor

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mc.designsystem.R
import com.mc.designsystem.components.MCCard
import com.mc.designsystem.components.MCTextField
import com.mc.designsystem.components.MCTextMenu
import com.mc.designsystem.theme.MoneyConvertorTheme
import com.mc.model.currency_convertor.CurrencyUiModel
import kotlinx.coroutines.launch

@Composable
fun CurrencyConvertorRoute(
    viewModel: CurrencyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        CircularProgressIndicator()
    } else {
        CurrencyConvertorScreen(
            uiState = uiState,
            onFromCurrencyCodeChange = viewModel::onFromCurrencyCodeChange,
            onFromCurrencyValueChange = viewModel::onFromCurrencyValueChange,
            onToCurrencyCodeChange = viewModel::onToCurrencyCodeChange,
            onToCurrencyValueChange = viewModel::onToCurrencyValueChange,
            swapCurrencies = viewModel::swapCurrencies
        )
    }
}

@Composable
internal fun CurrencyConvertorScreen(
    uiState: CurrencyConvertorUiState,
    onFromCurrencyCodeChange: (String) -> Unit,
    onFromCurrencyValueChange: (String) -> Unit,
    onToCurrencyCodeChange: (String) -> Unit,
    onToCurrencyValueChange: (String) -> Unit,
    swapCurrencies: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = stringResource(id = R.string.currency_convertor),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(100.dp))

        CurrencyConvertorCard(
            allCurrencies = uiState.allCurrencies,
            fromCurrency = uiState.fromCurrencyInfo,
            toCurrencyInfo = uiState.toCurrencyInfo,
            onFromCurrencyCodeChange = onFromCurrencyCodeChange,
            onFromCurrencyValueChange = onFromCurrencyValueChange,
            onToCurrencyCodeChange = onToCurrencyCodeChange,
            onToCurrencyValueChange = onToCurrencyValueChange,
            swapCurrencies = swapCurrencies
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(id = R.string.indicative_exhage_rate),
            style = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = uiState.indicativeExchangeRate,
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.Black
            )
        )

    }
}


@Composable
private fun CurrencyConvertorCard(
    modifier: Modifier = Modifier,
    allCurrencies: List<String>,
    fromCurrency: CurrencyUiModel,
    toCurrencyInfo: CurrencyUiModel,
    onFromCurrencyCodeChange: (String) -> Unit,
    onFromCurrencyValueChange: (String) -> Unit,
    onToCurrencyCodeChange: (String) -> Unit,
    onToCurrencyValueChange: (String) -> Unit,
    swapCurrencies: () -> Unit
) {
    MCCard(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        CurrencyInfoRow(
            label = stringResource(id = R.string.amount),
            selectedCurrency = fromCurrency,
            currencies = allCurrencies,
            onCurrencyCodeChange = onFromCurrencyCodeChange,
            onCurrencyValueChange = onFromCurrencyValueChange
        )

        Spacer(modifier = Modifier.height(20.dp))

        CurrenciesSwapper(
            onSwap = swapCurrencies
        )

        Spacer(modifier = Modifier.height(10.dp))

        CurrencyInfoRow(
            label = stringResource(id = R.string.indicative_exhage_rate),
            selectedCurrency = toCurrencyInfo,
            currencies = allCurrencies,
            onCurrencyCodeChange = onToCurrencyCodeChange,
            onCurrencyValueChange = onToCurrencyValueChange
        )
    }
}

@Composable
private fun CurrencyInfoRow(
    modifier: Modifier = Modifier,
    label: String,
    selectedCurrency: CurrencyUiModel,
    currencies: List<String>,
    onCurrencyCodeChange: (String) -> Unit,
    onCurrencyValueChange: (String) -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.height(15.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AnimatedContent(
                targetState = selectedCurrency.code,
                modifier = Modifier.weight(1f)
            ) {
                MCTextMenu(
                    selectedOption = it,
                    options = currencies,
                    onOptionSelected = { i -> onCurrencyCodeChange(currencies[i]) }
                )
            }

            Spacer(modifier = Modifier.width(30.dp))

            MCTextField(
                value = selectedCurrency.value,
                onValueChange = onCurrencyValueChange,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
            )
        }
    }
}

@Composable
private fun CurrenciesSwapper(
    modifier: Modifier = Modifier,
    onSwap: () -> Unit
) {
    val animatable = remember {
        Animatable(0f)
    }

    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        HorizontalDivider()
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable {
                    if (animatable.isRunning)
                        return@clickable

                    scope.launch {
                        onSwap()
                        animatable.animateTo(animatable.value + 180f)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .padding(10.dp)
                    .rotate(animatable.value),
                painter = painterResource(id = R.drawable.ic_trade),
                contentDescription = null,
                tint = Color.White
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyConvertorScreenPreview() {
    MoneyConvertorTheme {
//        CurrencyConvertorScreen(
//            uiState = CurrencyConvertorUiState.PreviewData,
//            onFromCurrencyChange = {},
//            onToCurrencyChange = {},
//            swapCurrencies = {}
//        )
    }
}

@Preview
@Composable
private fun CurrencyConvertorCardPreview() {
    MoneyConvertorTheme {
//        CurrencyConvertorCard(
//            allCurrencies = CurrencyUiModel.allCurrencies,
//            fromCurrency = CurrencyUiModel.allCurrencies.first(),
//            toCurrencyInfo = CurrencyUiModel.allCurrencies.first(),
//            onFromCurrencyInfoChange = { },
//            onToCurrencyInfoChange = { },
//            swapCurrencies = { }
//        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyInfoRowPreview() {
    MoneyConvertorTheme {
//        CurrencyInfoRow(
//            label = stringResource(id = R.string.amount),
//            selectedCurrency = CurrencyUiModel.allCurrencies.first(),
//            currencies = CurrencyUiModel.allCurrencies,
//            onCurrencyChange = {},
//        )
    }
}

@Preview
@Composable
private fun CurrenciesSwapper(modifier: Modifier = Modifier) {
    MoneyConvertorTheme {
        CurrenciesSwapper { }
    }
}