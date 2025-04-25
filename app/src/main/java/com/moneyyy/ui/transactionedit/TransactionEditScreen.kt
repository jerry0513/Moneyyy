package com.moneyyy.ui.transactionedit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneyyy.R
import com.moneyyy.data.Millis
import com.moneyyy.data.model.Category
import com.moneyyy.data.model.CategoryType
import com.moneyyy.data.model.ExpenseCategory
import com.moneyyy.data.model.IncomeCategory
import com.moneyyy.ui.component.LoadingDialog
import com.moneyyy.ui.component.MoneyyyDatePickerDialog
import com.moneyyy.ui.component.MoneyyyTopAppBar
import com.moneyyy.ui.theme.MoneyyyTheme

@Composable
fun TransactionEditScreen(
    uiState: TransactionEditUiState,
    onNavigationIconClick: () -> Unit = {},
    onTypeClick: (CategoryType) -> Unit = {},
    onCategoryClick: (Category) -> Unit = {},
    onNoteValueChange: (String) -> Unit = {},
    onDateMillisSelected: (Millis) -> Unit = {},
    onNumberClick: (String) -> Unit = {},
    onBackspaceClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {}
) {
    if (uiState.isLoading) {
        LoadingDialog()
    }

    Scaffold(
        topBar = {
            MoneyyyTopAppBar(
                title = {
                    var expanded by remember { mutableStateOf(false) }

                    TextButton(onClick = { expanded = !expanded }) {
                        Text(
                            text = when (uiState.categoryType) {
                                CategoryType.EXPENSE -> stringResource(R.string.expense)
                                CategoryType.INCOME -> stringResource(R.string.income)
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = stringResource(R.string.type)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(R.string.expense))
                            },
                            onClick = {
                                onTypeClick(CategoryType.EXPENSE)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(R.string.income))
                            },
                            onClick = {
                                onTypeClick(CategoryType.INCOME)
                                expanded = false
                            }
                        )
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
            var showDatePicker by remember {
                mutableStateOf(false)
            }

            Column {
                TransactionEditCategory(
                    categoryType = uiState.categoryType,
                    selectedCategory = uiState.category,
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp),
                    onItemClick = onCategoryClick
                )

                AnimatedVisibility(
                    visible = uiState.category != null,
                    enter = expandVertically() + slideInVertically { it },
                    exit = shrinkVertically() + slideOutVertically { it }
                ) {
                    TransactionEditKeyboard(
                        date = uiState.date!!,
                        note = uiState.note,
                        amount = uiState.amountText,
                        modifier = Modifier.testTag("transaction_edit_keyboard"),
                        onNoteValueChange = onNoteValueChange,
                        onDateClick = { showDatePicker = true },
                        onNumberClick = onNumberClick,
                        onBackspaceClick = onBackspaceClick,
                        onConfirmClick = onConfirmClick
                    )
                }
            }

            if (showDatePicker) {
                MoneyyyDatePickerDialog(
                    initialSelectedDateMillis = uiState.date?.correctedTimestamp,
                    onDateMillisSelected = onDateMillisSelected,
                    onDismiss = { showDatePicker = false }
                )
            }
        }
    }
}

@Composable
private fun TransactionEditCategory(
    categoryType: CategoryType,
    selectedCategory: Category?,
    modifier: Modifier = Modifier,
    onItemClick: (Category) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(
            when (categoryType) {
                CategoryType.EXPENSE -> ExpenseCategory.entries
                CategoryType.INCOME -> IncomeCategory.entries
            }
        ) {
            TransactionEditCategoryItem(
                category = it,
                isSelected = selectedCategory == it,
                onClick = onItemClick
            )
        }
    }
}

@Composable
private fun TransactionEditCategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: (Category) -> Unit
) {
    val name = stringResource(category.nameResId)

    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick(category) }
            .semantics(true) { contentDescription = name },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = CircleShape
                )
                .padding(10.dp)
        ) {
            Icon(
                painter = painterResource(category.iconResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionEditPreview() {
    MoneyyyTheme {
        TransactionEditScreen(
            uiState = TransactionEditUiState()
        )
    }
}