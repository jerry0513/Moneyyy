package com.moneyyy.ui.transactions

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneyyy.R
import com.moneyyy.data.model.ExpenseCategory
import com.moneyyy.ui.component.MyTopAppBar
import com.moneyyy.ui.component.LoadingView
import com.moneyyy.ui.theme.MoneyyyTheme

@Composable
fun TransactionsScreen(
    uiState: TransactionsUiState,
    onAddClick: () -> Unit = {},
    onItemClick: (Int) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val showFab = uiState is TransactionsUiState.Success && listState.isScrollInProgress.not()

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name))
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = showFab,
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
            when (uiState) {
                is TransactionsUiState.Loading -> {
                    LoadingView()
                }

                is TransactionsUiState.Success -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            top = 12.dp,
                            end = 12.dp,
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding() + 80.dp
                        ),
                    ) {
                        item {
                            TransactionsSectionSummary(uiState.info.summary)
                        }
                        items(
                            items = uiState.info.daily,
                            key = { it.date }
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(16.dp))
                                TransactionsSectionDaily(
                                    daily = it,
                                    onItemClick = onItemClick
                                )
                            }
                        }
                    }
                }
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
                .padding(vertical = 12.dp)
        ) {
            TransactionsSummaryItem(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.income),
                amount = summary.income
            )
            VerticalDivider(modifier = Modifier.padding(vertical = 8.dp))
            TransactionsSummaryItem(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.expense),
                amount = summary.expense
            )
            VerticalDivider(modifier = Modifier.padding(vertical = 8.dp))
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
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = amount.toString(),
            style = MaterialTheme.typography.titleLarge
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
                .background(Color.LightGray)
                .padding(6.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = item.note.ifEmpty {
                stringResource(item.category.nameResId)
            },
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = item.amountText,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(name = "loading")
@Composable
private fun TransactionsScreenLoadingPreview() {
    MoneyyyTheme {
        TransactionsScreen(
            uiState = TransactionsUiState.Loading
        )
    }
}

@Preview(name = "success")
@Composable
private fun TransactionsScreenSuccessPreview() {
    MoneyyyTheme {
        TransactionsScreen(
            uiState = TransactionsUiState.Success(
                TransactionsScreenInfo(
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