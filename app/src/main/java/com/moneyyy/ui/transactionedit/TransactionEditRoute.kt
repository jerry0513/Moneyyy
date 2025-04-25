package com.moneyyy.ui.transactionedit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moneyyy.R
import com.moneyyy.ui.util.SingleEventEffect
import com.moneyyy.ui.util.showToast

@Composable
fun TransactionEditRoute(
    viewModel: TransactionEditViewModel,
    onNavigationIconClick: () -> Unit,
    onEditSuccess: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SingleEventEffect(viewModel.uiEvent) {
        when (it) {
            TransactionEditUiEvent.EditSuccess -> {
                context.showToast(R.string.edit_success)
                onEditSuccess()
            }
            TransactionEditUiEvent.InvalidAmount -> {
                context.showToast(R.string.please_enter_amount)
            }
        }
    }

    TransactionEditScreen(
        uiState = uiState,
        onNavigationIconClick = onNavigationIconClick,
        onTypeClick = { viewModel.setType(it) },
        onCategoryClick = { viewModel.setCategory(it) },
        onNoteValueChange = { viewModel.setNote(it) },
        onDateMillisSelected = { viewModel.setDate(it) },
        onNumberClick = { viewModel.setNumber(it) },
        onBackspaceClick = { viewModel.backspaceNumber() },
        onConfirmClick = { viewModel.confirm() },
    )
}