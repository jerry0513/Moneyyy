package com.moneyyy.ui.transactions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TransactionsRoute(
    viewModel: TransactionsScreenViewModel,
    onAddClick: () -> Unit,
    onItemClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TransactionsScreen(
        uiState = uiState,
        onAddClick = onAddClick,
        onItemClick = onItemClick
    )
}