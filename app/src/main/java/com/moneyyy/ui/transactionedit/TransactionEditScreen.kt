package com.moneyyy.ui.transactionedit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneyyy.R
import com.moneyyy.data.Millis
import com.moneyyy.data.model.Category
import com.moneyyy.data.model.CategoryType
import com.moneyyy.data.model.ExpenseCategory
import com.moneyyy.data.model.IncomeCategory
import com.moneyyy.ui.component.LoadingDialog
import com.moneyyy.ui.component.LoadingView
import com.moneyyy.ui.component.MyDatePickerDialog
import com.moneyyy.ui.component.MyTopAppBar
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
    if (uiState.showLoadingDialog) {
        LoadingDialog()
    }

    Scaffold(
        topBar = {
            MyTopAppBar(
                title = {
                    var expanded by remember { mutableStateOf(false) }

                    TextButton(
                        onClick = { expanded = !expanded },
                        colors = ButtonDefaults.textButtonColors().copy(
                            containerColor = Color.LightGray
                        )
                    ) {
                        Text(
                            text = when (uiState.categoryType) {
                                CategoryType.EXPENSE -> stringResource(R.string.expense)
                                CategoryType.INCOME -> stringResource(R.string.income)
                            }
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

            if (uiState.showLoadingView) {
                LoadingView()
            }

            Column {
                TransactionEditCategory(
                    categoryType = uiState.categoryType,
                    selectedCategory = uiState.category,
                    modifier = Modifier.weight(1f),
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
                        onNoteValueChange = onNoteValueChange,
                        onDateClick = { showDatePicker = true },
                        onNumberClick = onNumberClick,
                        onBackspaceClick = onBackspaceClick,
                        onConfirmClick = onConfirmClick
                    )
                }
            }

            if (showDatePicker) {
                MyDatePickerDialog(
                    initialSelectedDateMillis = uiState.date?.dateMillis,
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
        modifier = modifier
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledIconButton(
            onClick = { onClick(category) },
            modifier = Modifier.size(48.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    Color.LightGray
                }
            )
        ) {
            Icon(
                painter = painterResource(category.iconResId),
                contentDescription = stringResource(R.string.category),
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Text(
            text = stringResource(category.nameResId),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionEditPreview() {
    MoneyyyTheme {
        TransactionEditScreen(
            uiState = TransactionEditUiState()
        )
    }
}