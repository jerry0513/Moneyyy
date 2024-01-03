package com.moneyyy.ui.recordedit

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneyyy.ui.component.MyTopAppBar
import com.moneyyy.R
import com.moneyyy.data.Millis
import com.moneyyy.ui.component.LoadingDialog
import com.moneyyy.ui.component.LoadingView
import com.moneyyy.ui.component.MyDatePickerDialog
import com.moneyyy.ui.theme.MoneyyyTheme

@Composable
fun RecordEditScreen(
    uiState: RecordEditUiState,
    onNavigationIconClicked: () -> Unit,
    onCategoryClicked: (String) -> Unit,
    onNoteValueChange: (String) -> Unit,
    onDateSelected: (Millis) -> Unit,
    onNumberClick: (String) -> Unit,
    onRemoveClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    if (uiState.showLoadingDialog) {
        LoadingDialog()
    }

    Scaffold(
        topBar = {
            MyTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClicked) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            content = {
                var showDatePicker by remember {
                    mutableStateOf(false)
                }

                if (uiState.showLoadingView) {
                    LoadingView()
                }

                Column {
                    val rememberOnCategoryClicked: (String) -> Unit = remember {
                        onCategoryClicked
                    }
                    val rememberOnNoteValueChange: (String) -> Unit = remember {
                        onNoteValueChange
                    }
                    val rememberOnNumberClick: (String) -> Unit = remember {
                        onNumberClick
                    }
                    val rememberOnRemoveClick: () -> Unit = remember {
                        onRemoveClick
                    }
                    val rememberOnConfirmClick: () -> Unit = remember {
                        onConfirmClick
                    }

                    RecordEditCategory(
                        category = uiState.category,
                        modifier = Modifier.weight(1f),
                        onItemClick = rememberOnCategoryClicked
                    )
                    InputLayout(
                        date = uiState.date,
                        note = uiState.note,
                        price = uiState.price,
                        onNoteValueChange = rememberOnNoteValueChange,
                        onDateClick = { showDatePicker = true },
                        onNumberClick = rememberOnNumberClick,
                        onRemoveClick = rememberOnRemoveClick,
                        onConfirmClick = rememberOnConfirmClick
                    )
                }

                if (showDatePicker) {
                    MyDatePickerDialog(
                        onDateSelected = onDateSelected,
                        onDismiss = { showDatePicker = false }
                    )
                }
            }
        )
    }
}

@Composable
private fun RecordEditCategory(
    category: String,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = modifier
    ) {
        items(nameToIconResId) { (name, iconResId) ->
            val rememberOnClick = remember(name) {
                onItemClick
            }

            RecordEditCategoryItem(
                name = name,
                iconResId = iconResId,
                isSelected = category == name,
                onClick = rememberOnClick
            )
        }
    }
}

@Composable
private fun RecordEditCategoryItem(
    name: String,
    @DrawableRes iconResId: Int,
    isSelected: Boolean,
    onClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledIconButton(
            onClick = { onClick(name) },
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
                painter = painterResource(iconResId),
                contentDescription = stringResource(R.string.category),
                modifier = Modifier.size(28.dp),
                tint = Color.Black
            )
        }
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun InputLayout(
    date: String,
    note: String,
    price: String,
    onNoteValueChange: (String) -> Unit,
    onDateClick: () -> Unit,
    onNumberClick: (String) -> Unit,
    onRemoveClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        Divider()
        Row(
            modifier = Modifier
                .padding(start = 8.dp, top = 12.dp, end = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = note,
                onValueChange = onNoteValueChange,
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium,
                singleLine = true,
                decorationBox = { innerTextField ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "note",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.width(8.dp))
                        Box {
                            if (note.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.note),
                                    color = Color.LightGray,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                            innerTextField()
                        }
                    }
                }
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = price,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.End
            )
        }
        Divider()
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("7") }
            ) {
                Text(text = "7")
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("8") }
            ) {
                Text(text = "8")
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("9") }
            ) {
                Text(text = "9")
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = onDateClick
            ) {
                Text(text = date)
            }
        }
        Divider()
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("4") }
            ) {
                Text(text = "4")
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("5") }
            ) {
                Text(text = "5")
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("6") }
            ) {
                Text(text = "6")
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(modifier = Modifier.weight(1f))
        }
        Divider()
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("1") }
            ) {
                Text(text = "1")
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("2") }
            ) {
                Text(text = "2")
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("3") }
            ) {
                Text(text = "3")
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(modifier = Modifier.weight(1f))
        }
        Divider()
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            KeyboardItemContainer(modifier = Modifier.weight(1f))
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("0") }
            ) {
                Text(text = "0")
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = onRemoveClick::invoke
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowLeft,
                    contentDescription = stringResource(R.string.backspace),
                )
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = onConfirmClick::invoke
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = stringResource(R.string.edit),
                )
            }
        }
        Divider()
    }
}

@Composable
fun KeyboardItemContainer(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit = {}
) {
    val rememberOnClick = remember {
        onClick
    }
    Column(
        modifier = modifier
            .height(50.dp)
            .clickable(onClick = rememberOnClick),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
private fun KeyboardVerticalDivider() {
    Divider(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun RecordEditPreview() {
    MoneyyyTheme {
        RecordEditScreen(
            uiState = RecordEditUiState(),
            onNavigationIconClicked = {},
            onCategoryClicked = {},
            onNoteValueChange = {},
            onDateSelected = {},
            onNumberClick = {},
            onRemoveClick = {},
            onConfirmClick = {}
        )
    }
}

val nameToIconResId = listOf(
    "餐飲" to R.drawable.ic_food,
    "交通" to R.drawable.ic_transportation,
    "車" to R.drawable.ic_car,
    "娛樂" to R.drawable.ic_entertainment,
    "購物" to R.drawable.ic_shopping,
    "電話費" to R.drawable.ic_phone_bill,
)