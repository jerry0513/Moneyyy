package com.moneyyy.ui.record

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.moneyyy.R
import com.moneyyy.ui.util.SingleEventEffect
import com.moneyyy.ui.util.showToast

@Composable
fun RecordRoute(
    viewModel: RecordScreenViewModel,
    onNavigationIconClicked: () -> Unit,
    onDeleted: () -> Unit,
    onEditClicked: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    SingleEventEffect(viewModel.onDeleteSuccessful) {
        context.showToast(R.string.delete_success)
        onDeleted()
    }

    RecordScreen(
        uiState = uiState,
        onNavigationIconClicked = onNavigationIconClicked,
        onDeleteClicked = { viewModel.deleteRecord() },
        onEditClicked = onEditClicked
    )
}