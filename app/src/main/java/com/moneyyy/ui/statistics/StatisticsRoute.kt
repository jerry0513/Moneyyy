package com.moneyyy.ui.statistics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun StatisticsRoute(
    viewModel: StatisticsViewModel,
    onNavigationIconClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    StatisticsScreen(
        uiState = uiState,
        onNavigationIconClick = onNavigationIconClick,
        onYearMonthSelected = { year, month -> viewModel.setDate(year, month) },
        onSwapClick = { viewModel.swapType() }
    )
}