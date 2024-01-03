package com.moneyyy.ui.records

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneyyy.R
import com.moneyyy.ui.component.MyTopAppBar
import com.moneyyy.ui.component.LoadingView
import com.moneyyy.ui.theme.MoneyyyTheme
import com.moneyyy.ui.util.isScrollingUp

@Composable
fun RecordsScreen(
    uiState: RecordsUiState,
    onAddRecordClick: () -> Unit,
    onRecordClick: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = {
                    Text(text = "Moneyyy")
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = listState.isScrollingUp(),
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(onClick = onAddRecordClick) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add_record)
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
                is RecordsUiState.Loading -> {
                    LoadingView()
                }

                is RecordsUiState.Success -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(
                            start = 12.dp,
                            top = 12.dp,
                            end = 12.dp,
                            bottom = 32.dp
                        ),
                    ) {
                        item {
                            RecordsSummary(uiState.info.summary)
                        }
                        items(
                            key = { it.date },
                            items = uiState.info.dailyInfo,
                        ) {
                            Column {
                                Spacer(modifier = Modifier.height(16.dp))
                                RecordsItem(
                                    info = it,
                                    onClick = onRecordClick
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
private fun RecordsSummary(info: RecordsSectionSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            RecordsSummaryItem(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.income),
                number = info.incomeSum.toString()
            )
            RecordsSummaryItem(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.expense),
                number = info.expenseSum.toString()
            )
            RecordsSummaryItem(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.balance),
                number = info.balance.toString()
            )
        }
    }
}

@Composable
private fun RecordsSummaryItem(
    modifier: Modifier = Modifier,
    title: String,
    number: String
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
            text = number,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun RecordsItem(
    info: RecordsSectionDailyInfo,
    onClick: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = info.date,
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = info.balance.toString(),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.Gray)
            )
            info.items.forEach {
                key(it.id) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onClick(it.id) }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = it.category.ifEmpty { it.note },
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = it.priceText,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecordsPreview() {
    MoneyyyTheme {
        RecordsScreen(
            RecordsUiState.Loading,
            {},
            {}
        )
    }
}