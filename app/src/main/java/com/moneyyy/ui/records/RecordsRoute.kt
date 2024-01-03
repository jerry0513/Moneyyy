package com.moneyyy.ui.records

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun RecordsRoute(
    viewModel: RecordsScreenViewModel,
    onAddRecordClick: () -> Unit,
    onRecordClick: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    RecordsScreen(
        uiState = uiState,
        onAddRecordClick = onAddRecordClick,
        onRecordClick = onRecordClick
    )
}