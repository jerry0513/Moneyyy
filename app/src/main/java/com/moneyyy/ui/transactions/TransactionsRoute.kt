package com.moneyyy.ui.transactions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneyyy.ui.util.SingleEventEffect

@Composable
fun TransactionsRoute(
    viewModel: TransactionsViewModel,
    onStatisticsClick: (year: Int, month: Int) -> Unit,
    onAddClick: () -> Unit,
    onItemClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SingleEventEffect(viewModel.uiEvent) {
        when (it) {
            is TransactionsUiEvent.NavigateToStatistics -> onStatisticsClick(it.year, it.month)
        }
    }

    TransactionsScreen(
        uiState = uiState,
        onStatisticsClick = { viewModel.clickStatistics() },
        onYearMonthSelected = { year, month -> viewModel.setDate(year, month) },
        onAddClick = onAddClick,
        onItemClick = onItemClick
    )
}