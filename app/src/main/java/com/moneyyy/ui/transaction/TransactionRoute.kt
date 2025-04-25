package com.moneyyy.ui.transaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneyyy.R
import com.moneyyy.ui.util.SingleEventEffect
import com.moneyyy.ui.util.showToast

@Composable
fun TransactionRoute(
    viewModel: TransactionViewModel,
    onNavigationIconClick: () -> Unit,
    onDeleted: () -> Unit,
    onEditClick: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SingleEventEffect(viewModel.uiEvent) {
        when (it) {
            TransactionUiEvent.DeleteSuccess -> {
                context.showToast(R.string.delete_success)
                onDeleted()
            }
        }
    }

    TransactionScreen(
        uiState = uiState,
        onNavigationIconClick = onNavigationIconClick,
        onDeleteClick = { viewModel.delete() },
        onEditClick = onEditClick
    )
}