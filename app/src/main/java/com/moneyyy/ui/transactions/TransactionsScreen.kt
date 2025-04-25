package com.moneyyy.ui.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moneyyy.R
import com.moneyyy.data.model.ExpenseCategory
import com.moneyyy.ui.component.LoadingOverlay
import com.moneyyy.ui.component.MoneyyyTopAppBar
import com.moneyyy.ui.component.YearMonthPicker
import com.moneyyy.ui.theme.MoneyyyTheme
import java.time.LocalDate

@Composable
fun TransactionsScreen(
    uiState: TransactionsUiState,
    onStatisticsClick: () -> Unit = {},
    onYearMonthSelected: (Int, Int) -> Unit = { _, _ -> },
    onAddClick: () -> Unit = {},
    onItemClick: (Int) -> Unit = {}
) {
    val listState = rememberLazyListState()
    var showYearMonthPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MoneyyyTopAppBar(
                title = {
                    Row {
                        IconButton(onClick = onStatisticsClick) {
                            Icon(
                                painter = painterResource(R.drawable.ic_statistics),
                                contentDescription = stringResource(R.string.statistics)
                            )
                        }
                        TextButton(onClick = { showYearMonthPicker = !showYearMonthPicker }) {
                            val pickerArrowRotationAngle by animateFloatAsState(
                                targetValue = if (showYearMonthPicker) 180f else 0f
                            )

                            Text(
                                text = stringResource(
                                    R.string.format_transactions_screen_month_year,
                                    uiState.info.date.month,
                                    uiState.info.date.year
                                ),
                                style = MaterialTheme.typography.labelMedium
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = stringResource(R.string.year_month_picker),
                                modifier = Modifier.rotate(pickerArrowRotationAngle)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = listState.isScrollInProgress.not() && !showYearMonthPicker,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(onClick = onAddClick) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_transaction)
                    )
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(
                    start = 12.dp,
                    top = 12.dp,
                    end = 12.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues()
                        .calculateBottomPadding() + 80.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    TransactionsSectionSummary(uiState.info.summary)
                }

                if (uiState.errorMessage.isEmpty()) {
                    items(
                        items = uiState.info.daily,
                        key = { it.date }
                    ) {
                        TransactionsSectionDaily(
                            daily = it,
                            onItemClick = onItemClick
                        )
                    }
                } else {
                    item {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                LoadingOverlay(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background.copy(0.5f))
                )
            }

            if (showYearMonthPicker) {
                YearMonthPicker(
                    initialYear = uiState.info.date.year,
                    initialMonth = uiState.info.date.month,
                    onDismiss = {
                        showYearMonthPicker = false
                    },
                    onYearMonthSelected = { year, month ->
                        showYearMonthPicker = false
                        onYearMonthSelected(year, month)
                    }
                )
            }
        }
    }
}

@Composable
private fun TransactionsSectionSummary(summary: TransactionsSummary) {
    ElevatedCard {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TransactionsSummaryItem(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.income),
                amount = summary.income
            )
            VerticalDivider()
            TransactionsSummaryItem(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.expense),
                amount = summary.expense
            )
            VerticalDivider()
            TransactionsSummaryItem(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.balance),
                amount = summary.balance
            )
        }
    }
}

@Composable
private fun TransactionsSummaryItem(
    modifier: Modifier = Modifier,
    title: String,
    amount: Int
) {
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = amount.toString(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium.copy(
                lineHeight = 18.sp
            )
        )
    }
}

@Composable
private fun TransactionsSectionDaily(
    daily: TransactionsDaily,
    onItemClick: (Int) -> Unit
) {
    ElevatedCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = daily.date,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = daily.summary.balance.toString(),
                style = MaterialTheme.typography.labelMedium
            )
        }
        HorizontalDivider()
        for (item in daily.items) {
            key(item.id) {
                TransactionsDailyItem(
                    item = item,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onItemClick
                )
            }
        }
    }
}

@Composable
private fun TransactionsDailyItem(
    item: TransactionsDailyItem,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    Row(
        modifier = modifier
            .clickable { onClick(item.id) }
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(item.category.iconResId),
            contentDescription = stringResource(item.category.nameResId),
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(6.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = item.note.ifEmpty {
                stringResource(item.category.nameResId)
            },
            modifier = Modifier.weight(1f),
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = item.amountText,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview()
@Composable
private fun TransactionsScreenLoadingPreview() {
    MoneyyyTheme {
        TransactionsScreen(
            uiState = TransactionsUiState(
                info = TransactionsScreenInfo(
                    date = LocalDate.now().let {
                        TransactionsDate(year = it.year, month = it.monthValue)
                    },
                    summary = TransactionsSummary(income = 0, expense = 0, balance = 0),
                    daily = listOf()
                ),
                isLoading = true
            )
        )
    }
}

@Preview()
@Composable
private fun TransactionsScreenSuccessPreview() {
    MoneyyyTheme {
        TransactionsScreen(
            uiState = TransactionsUiState(
                TransactionsScreenInfo(
                    date = TransactionsDate(
                        year = 2025,
                        month = 5
                    ),
                    summary = TransactionsSummary(
                        income = 1000,
                        expense = 10000,
                        balance = -9000
                    ),
                    daily = listOf(
                        TransactionsDaily(
                            date = "2025-04-30",
                            summary = TransactionsSummary(
                                income = 4495,
                                expense = 2777,
                                balance = 8022
                            ),
                            items = listOf(
                                TransactionsDailyItem(
                                    id = 3190,
                                    category = ExpenseCategory.CAR,
                                    note = "doming",
                                    amountText = "99"
                                ),
                                TransactionsDailyItem(
                                    id = 3190,
                                    category = ExpenseCategory.SHOPPING,
                                    note = "",
                                    amountText = "999"
                                )
                            )
                        ),
                        TransactionsDaily(
                            date = "2025-04-29",
                            summary = TransactionsSummary(
                                income = 3070,
                                expense = 7079,
                                balance = 8469
                            ),
                            items = listOf(
                                TransactionsDailyItem(
                                    id = 4740,
                                    category = ExpenseCategory.TELEPHONE,
                                    note = "doming",
                                    amountText = "123"
                                )
                            )
                        )
                    )
                )
            )
        )
    }
}