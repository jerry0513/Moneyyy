package com.moneyyy.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneyyy.R
import com.moneyyy.data.model.CategoryType
import com.moneyyy.data.model.ExpenseCategory
import com.moneyyy.ui.component.LoadingDialog
import com.moneyyy.ui.component.LoadingOverlay
import com.moneyyy.ui.component.MoneyyyTopAppBar
import com.moneyyy.ui.theme.MoneyyyTheme

@Composable
fun TransactionScreen(
    uiState: TransactionUiState,
    onNavigationIconClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    if (uiState.isDeleting) {
        LoadingDialog()
    }

    Scaffold(
        topBar = {
            MoneyyyTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                actions = {
                    if (uiState.info != null) {
                        IconButton(onClick = onDeleteClick) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
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
            when {
                uiState.isInitialLoading -> LoadingOverlay()
                uiState.info != null -> {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Box(modifier = Modifier.padding(top = 12.dp, bottom = 40.dp)) {
                            ElevatedCard {
                                TransactionScreenInfo(uiState.info)
                            }
                            FloatingActionButton(
                                onClick = onEditClick,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .offset(x = (-16).dp, y = 28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = stringResource(R.string.edit)
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
private fun TransactionScreenInfo(info: TransactionScreenInfo) {
    Column(modifier = Modifier.padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 40.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.widthIn(min = 80.dp)) {
                Icon(
                    painter = painterResource(info.category.iconResId),
                    contentDescription = stringResource(info.category.nameResId),
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(6.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = stringResource(info.category.nameResId),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(20.dp))
        TransactionDetailItem(
            title = stringResource(R.string.category),
            value = stringResource(
                id = when (info.categoryType) {
                    CategoryType.INCOME -> R.string.income
                    CategoryType.EXPENSE -> R.string.expense
                }
            )
        )
        Spacer(modifier = Modifier.height(20.dp))
        TransactionDetailItem(
            title = stringResource(R.string.amount),
            value = info.amount.toString()
        )
        Spacer(modifier = Modifier.height(20.dp))
        TransactionDetailItem(
            title = stringResource(R.string.date),
            value = info.date
        )
        Spacer(modifier = Modifier.height(20.dp))
        TransactionDetailItem(
            title = stringResource(id = R.string.note),
            value = info.note
        )
    }
}

@Composable
private fun TransactionDetailItem(
    title: String,
    value: String,
) {
    Row {
        Text(
            text = title,
            modifier = Modifier.widthIn(min = 80.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(name = "initial loading")
@Composable
private fun TransactionScreenInitialLoadingPreview() {
    MoneyyyTheme {
        TransactionScreen(
            TransactionUiState(isInitialLoading = true)
        )
    }
}

@Preview(name = "action loading")
@Composable
private fun TransactionScreenActionLoadingPreview() {
    MoneyyyTheme {
        TransactionScreen(
            TransactionUiState(isDeleting = true)
        )
    }
}

@Preview(name = "info")
@Composable
private fun TransactionScreenInfoPreview() {
    MoneyyyTheme {
        TransactionScreen(
            TransactionUiState(
                info = TransactionScreenInfo(
                    category = ExpenseCategory.SHOPPING,
                    categoryType = CategoryType.EXPENSE,
                    amount = 6287,
                    date = "2025-04-30",
                    note = "hello"
                )
            )
        )
    }
}