package com.moneyyy.ui.transactionedit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneyyy.R
import com.moneyyy.ui.theme.MoneyyyTheme

@Composable
fun TransactionEditKeyboard(
    date: TransactionEditDate,
    note: String,
    amount: String,
    modifier: Modifier = Modifier,
    onNoteValueChange: (String) -> Unit = {},
    onDateClick: () -> Unit = {},
    onNumberClick: (String) -> Unit = {},
    onBackspaceClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        KeyboardHorizontalDivider()
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 12.dp, end = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = note,
                onValueChange = onNoteValueChange,
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true,
                decorationBox = { innerTextField ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = stringResource(R.string.note),
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Box {
                            if (note.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.note),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            innerTextField()
                        }
                    }
                }
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = amount,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.End
            )
        }
        KeyboardHorizontalDivider()
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("7") }
            ) {
                Text(
                    text = "7",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("8") }
            ) {
                Text(
                    text = "8",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("9") }
            ) {
                Text(
                    text = "9",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = onDateClick
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = date.year.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = stringResource(R.string.format_edit_screen_day_month, date.month, date.dayOfMonth),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
        KeyboardHorizontalDivider()
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("4") }
            ) {
                Text(
                    text = "4",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("5") }
            ) {
                Text(
                    text = "5",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("6") }
            ) {
                Text(
                    text = "6",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(modifier = Modifier.weight(1f))
        }
        KeyboardHorizontalDivider()
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("1") }
            ) {
                Text(
                    text = "1",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("2") }
            ) {
                Text(
                    text = "2",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("3") }
            ) {
                Text(
                    text = "3",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(modifier = Modifier.weight(1f))
        }
        KeyboardHorizontalDivider()
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            KeyboardItemContainer(modifier = Modifier.weight(1f))
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = { onNumberClick("0") }
            ) {
                Text(
                    text = "0",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = onBackspaceClick
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_backspace),
                    contentDescription = stringResource(R.string.backspace),
                )
            }
            KeyboardVerticalDivider()
            KeyboardItemContainer(
                modifier = Modifier.weight(1f),
                onClick = onConfirmClick
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = stringResource(R.string.edit),
                )
            }
        }
        KeyboardHorizontalDivider()
    }
}

@Composable
private fun KeyboardHorizontalDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceDim)
}

@Composable
private fun KeyboardVerticalDivider() {
    VerticalDivider(color = MaterialTheme.colorScheme.surfaceDim)
}

@Composable
private fun KeyboardItemContainer(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Column(
        modifier = modifier
            .height(56.dp)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Preview
@Composable
private fun TransactionEditInputLayoutPreview() {
    MoneyyyTheme {
        TransactionEditKeyboard(
            date = TransactionEditDate(
                year = 2025,
                month = 5,
                dayOfMonth = 11,
                timestamp = 1746896392000,
                correctedTimestamp = 1746896392000
            ),
            note = "",
            amount = "123"
        )
    }
}