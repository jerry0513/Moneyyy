package com.moneyyy.ui.statistics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneyyy.R
import com.moneyyy.data.model.CategoryType
import com.moneyyy.data.model.ExpenseCategory
import com.moneyyy.ui.component.LoadingOverlay
import com.moneyyy.ui.component.MoneyyyTopAppBar
import com.moneyyy.ui.theme.MoneyyyTheme
import com.moneyyy.ui.component.YearMonthPicker

@Composable
fun StatisticsScreen(
    uiState: StatisticsUiState,
    onNavigationIconClick: () -> Unit = {},
    onYearMonthSelected: (Int, Int) -> Unit = { _, _ -> },
    onSwapClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    var showYearMonthPicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MoneyyyTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                        IconButton(
                            onClick = onSwapClick,
                            enabled = uiState.isLoading.not()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_swap_horiz),
                                contentDescription = stringResource(R.string.swap)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatisticsSectionPieChart(uiState.info.topCategoryItems)

                if (uiState.errorMessage.isEmpty()) {
                    StatisticsSectionCategory(
                        categoryType = uiState.info.categoryType,
                        amount = uiState.info.amount,
                        items = uiState.info.categoryItems
                    )
                } else {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
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
private fun StatisticsSectionPieChart(items: List<StatisticsCategoryItem>) {
    ElevatedCard {
        StatisticsPieChart(
            data = items,
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 24.dp)
                .heightIn(min = 140.dp)
        )
    }
}

@Composable
private fun StatisticsSectionCategory(
    categoryType: CategoryType,
    amount: Int,
    items: List<StatisticsCategoryItem>
) {
    ElevatedCard {
        Row(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(
                    when (categoryType) {
                        CategoryType.EXPENSE -> R.string.expense
                        CategoryType.INCOME -> R.string.income
                    }
                ),
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = amount.toString(),
                style = MaterialTheme.typography.labelMedium
            )
        }

        for (item in items) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(item.category!!.iconResId),
                    contentDescription = stringResource(item.category.nameResId),
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(6.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = stringResource(item.category.nameResId),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.format_percentage, item.percentageValue),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = item.amount.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
private fun StatisticsScreenInitialLoadingPreview() {
    MoneyyyTheme {
        StatisticsScreen(
            uiState = StatisticsUiState(
                info = StatisticsScreenInfo(
                    date = StatisticsDate(2025, 5),
                    categoryType = CategoryType.EXPENSE,
                    amount = 0,
                    topCategoryItems = listOf(),
                    categoryItems = listOf()
                ),
                isLoading = true
            )
        )
    }
}

@Preview
@Composable
private fun StatisticsScreenActionLoadingPreview() {
    MoneyyyTheme {
        StatisticsScreen(
            uiState = StatisticsUiState(
                info = StatisticsScreenInfo(
                    date = StatisticsDate(2025, 5),
                    categoryType = CategoryType.EXPENSE,
                    amount = 1000,
                    topCategoryItems = listOf(
                        StatisticsCategoryItem(
                            category = ExpenseCategory.CAR,
                            amount = 500,
                            percentageValue = 50f
                        ),
                        StatisticsCategoryItem(
                            category = ExpenseCategory.TELEPHONE,
                            amount = 300,
                            percentageValue = 30f
                        ),
                        StatisticsCategoryItem(
                            category = ExpenseCategory.SHOPPING,
                            amount = 200,
                            percentageValue = 20f
                        ),
                    ),
                    categoryItems = listOf(
                        StatisticsCategoryItem(
                            category = ExpenseCategory.CAR,
                            amount = 500,
                            percentageValue = 50f
                        ),
                        StatisticsCategoryItem(
                            category = ExpenseCategory.TELEPHONE,
                            amount = 300,
                            percentageValue = 30f
                        ),
                        StatisticsCategoryItem(
                            category = ExpenseCategory.SHOPPING,
                            amount = 200,
                            percentageValue = 20f
                        ),
                    )
                ),
                isLoading = true
            )
        )
    }
}

@Preview
@Composable
private fun StatisticsScreenSuccessPreview() {
    MoneyyyTheme {
        StatisticsScreen(
            uiState = StatisticsUiState(
                info = StatisticsScreenInfo(
                    date = StatisticsDate(2025, 5),
                    categoryType = CategoryType.EXPENSE,
                    amount = 1000,
                    topCategoryItems = listOf(
                        StatisticsCategoryItem(
                            category = ExpenseCategory.CAR,
                            amount = 500,
                            percentageValue = 50f
                        ),
                        StatisticsCategoryItem(
                            category = ExpenseCategory.TELEPHONE,
                            amount = 300,
                            percentageValue = 30f
                        ),
                        StatisticsCategoryItem(
                            category = ExpenseCategory.SHOPPING,
                            amount = 200,
                            percentageValue = 20f
                        ),
                    ),
                    categoryItems = listOf(
                        StatisticsCategoryItem(
                            category = ExpenseCategory.CAR,
                            amount = 500,
                            percentageValue = 50f
                        ),
                        StatisticsCategoryItem(
                            category = ExpenseCategory.TELEPHONE,
                            amount = 300,
                            percentageValue = 30f
                        ),
                        StatisticsCategoryItem(
                            category = ExpenseCategory.SHOPPING,
                            amount = 200,
                            percentageValue = 20f
                        ),
                    )
                )
            )
        )
    }
}

@Preview
@Composable
private fun StatisticsScreenErrorPreview() {
    MoneyyyTheme {
        StatisticsScreen(
            uiState = StatisticsUiState(
                info = StatisticsScreenInfo(
                    date = StatisticsDate(2025, 5),
                    categoryType = CategoryType.EXPENSE,
                    amount = 0,
                    topCategoryItems = listOf(),
                    categoryItems = listOf()
                ),
                errorMessage = "error message"
            )
        )
    }
}