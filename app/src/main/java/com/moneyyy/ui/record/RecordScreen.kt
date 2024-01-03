package com.moneyyy.ui.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.moneyyy.ui.component.MyTopAppBar
import com.moneyyy.R
import com.moneyyy.data.price.PriceType
import com.moneyyy.ui.component.LoadingDialog
import com.moneyyy.ui.component.LoadingView
import com.moneyyy.ui.theme.MoneyyyTheme

@Composable
fun RecordScreen(
    uiState: RecordUiState,
    onNavigationIconClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onEditClicked: () -> Unit
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
                },
                actions = {
                    IconButton(onClick = onDeleteClicked) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.delete)
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
            if (uiState.showLoadingView) {
                LoadingView()
            }

            if (uiState.info != null) {
                ConstraintLayout(
                    modifier = Modifier.padding(12.dp)
                ) {
                    val (card, floatingActionButton) = createRefs()
                    Card(
                        modifier = Modifier
                            .constrainAs(card) {
                                top.linkTo(parent.top)
                            }
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(
                                    start = 16.dp,
                                    top = 24.dp,
                                    end = 16.dp,
                                    bottom = 40.dp
                                )
                        ) {
                            Text(text = uiState.info.category)
                            Spacer(modifier = Modifier.height(12.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(20.dp))
                            RecordScreenItem(
                                title = stringResource(R.string.category),
                                value = stringResource(
                                    id = when (uiState.info.priceInfo.type) {
                                        PriceType.INCOME -> R.string.income
                                        PriceType.EXPENSE -> R.string.expense
                                    }
                                )
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            RecordScreenItem(
                                title = stringResource(R.string.price),
                                value = uiState.info.priceInfo.price.toString()
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            RecordScreenItem(
                                title = stringResource(R.string.date),
                                value = uiState.info.date
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            RecordScreenItem(
                                title = stringResource(id = R.string.note),
                                value = uiState.info.note
                            )
                        }
                    }
                    FloatingActionButton(
                        onClick = onEditClicked,
                        modifier = Modifier
                            .constrainAs(floatingActionButton) {
                                bottom.linkTo(card.bottom)
                                end.linkTo(card.end, 16.dp)
                                top.linkTo(card.bottom)
                            }
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

@Composable
fun RecordScreenItem(
    title: String,
    value: String,
) {
    Row {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.width(40.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecordScreenPreview() {
    MoneyyyTheme {
        RecordScreen(
            RecordUiState(),
            {},
            {},
            {}
        )
    }
}