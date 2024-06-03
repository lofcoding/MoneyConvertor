package com.loc.currencyconvertor

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mc.designsystem.R
import com.mc.designsystem.components.MCBackgroundScreen
import com.mc.designsystem.components.MCCard
import com.mc.designsystem.components.MCTextField
import com.mc.designsystem.components.MCTextMenu
import com.mc.designsystem.theme.MoneyConvertorTheme
import kotlinx.coroutines.launch

@Composable
fun CurrencyConvertorRoute(
    viewModel: CurrencyConvertorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CurrencyConvertorScreen(
        uiState = uiState,
        onFromCurrencyChange = viewModel::onFromCurrencyChange,
        onToCurrencyChange = viewModel::onToCurrencyChange,
        swapCurrencies = viewModel::swapCurrencies
    )
}

@Composable
internal fun CurrencyConvertorScreen(
    uiState: CurrencyConvertorUiState,
    onFromCurrencyChange: (CurrencyUiModel) -> Unit,
    onToCurrencyChange: (CurrencyUiModel) -> Unit,
    swapCurrencies: () -> Unit
) {
    MCBackgroundScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = stringResource(id = R.string.currency_convertor),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(id = R.string.currency_convertor_description),
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center,
                color = Color(0xff808080),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(50.dp))

            CurrencyConvertorCard(
                allCurrencies = uiState.allCurrencies,
                fromCurrency = uiState.fromCurrency,
                toCurrencyInfo = uiState.toCurrency,
                onFromCurrencyChange = onFromCurrencyChange,
                onToCurrencyChange = onToCurrencyChange,
                swapCurrencies = swapCurrencies
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                modifier = Modifier.padding(horizontal = 22.dp),
                text = "${stringResource(id = R.string.indicative_exhage_rate)} ${uiState.lastUpdated}",
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                modifier = Modifier.padding(horizontal = 22.dp),
                text = uiState.indicativeExchangeRate,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.Black
                )
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("loading")
                    )
                }
            }
        }
    }
}


@Composable
private fun CurrencyConvertorCard(
    modifier: Modifier = Modifier,
    allCurrencies: List<CurrencyUiModel>,
    fromCurrency: CurrencyUiModel,
    toCurrencyInfo: CurrencyUiModel,
    onFromCurrencyChange: (CurrencyUiModel) -> Unit,
    onToCurrencyChange: (CurrencyUiModel) -> Unit,
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
            onCurrencyChange = onFromCurrencyChange
        )

        Spacer(modifier = Modifier.height(20.dp))

        CurrenciesSwapper(
            onSwap = swapCurrencies
        )

        Spacer(modifier = Modifier.height(10.dp))

        CurrencyInfoRow(
            label = stringResource(id = R.string.converted_amount),
            selectedCurrency = toCurrencyInfo,
            currencies = allCurrencies,
            onCurrencyChange = onToCurrencyChange
        )
    }
}

@Composable
private fun CurrencyInfoRow(
    modifier: Modifier = Modifier,
    label: String,
    selectedCurrency: CurrencyUiModel,
    currencies: List<CurrencyUiModel>,
    onCurrencyChange: (CurrencyUiModel) -> Unit,
) {
    val currencyCodes = remember(currencies) {
        currencies.map { it.code }
    }
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
                modifier = Modifier.weight(1f),
                transitionSpec = {
                    (fadeIn(animationSpec = tween(300, delayMillis = 90)) +
                            scaleIn(
                                initialScale = 0.92f,
                                animationSpec = tween(300, delayMillis = 90)
                            ))
                        .togetherWith(fadeOut(animationSpec = tween(90)))
                }
            ) {
                MCTextMenu(
                    selectedOption = it,
                    options = currencyCodes,
                    onOptionSelected = { i ->
                        onCurrencyChange(currencies[i].copy(value = selectedCurrency.value))
                    }
                )
            }

            Spacer(modifier = Modifier.width(30.dp))

            MCTextField(
                value = selectedCurrency.value,
                onValueChange = { onCurrencyChange(selectedCurrency.copy(value = it)) },
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
                        animatable.animateTo(
                            animatable.value + 180f,
                            animationSpec = tween(300)
                        )
                    }
                }
                .testTag("swap"),
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
        CurrencyConvertorScreen(
            uiState = CurrencyConvertorUiState.PreviewData,
            onFromCurrencyChange = {},
            onToCurrencyChange = {},
            swapCurrencies = {}
        )
    }
}