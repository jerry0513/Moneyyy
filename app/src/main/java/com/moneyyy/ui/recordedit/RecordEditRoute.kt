package com.moneyyy.ui.recordedit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.moneyyy.R
import com.moneyyy.ui.util.SingleEventEffect
import com.moneyyy.ui.util.showToast

@Composable
fun RecordEditRoute(
    viewModel: RecordEditViewModel,
    onNavigationIconClicked: () -> Unit,
    onEditingSuccessful: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    SingleEventEffect(viewModel.onEditingSuccessful) {
        context.showToast(R.string.edit_success)
        onEditingSuccessful()
    }

    RecordEditScreen(
        uiState = uiState,
        onNavigationIconClicked = onNavigationIconClicked,
        onCategoryClicked = viewModel::setCategory,
        onNoteValueChange = viewModel::setNote,
        onDateSelected = viewModel::setDate,
        onNumberClick = viewModel::setNumber,
        onRemoveClick = viewModel::removeNumber,
        onConfirmClick = viewModel::confirmRecord,
    )
}