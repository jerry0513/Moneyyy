package com.moneyyy.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneyyy.data.repository.TransactionRepository
import com.moneyyy.data.model.CategoryType
import com.moneyyy.data.database.TransactionEntity
import com.moneyyy.data.model.ExpenseCategory
import com.moneyyy.data.model.IncomeCategory
import com.moneyyy.ui.transactions.TransactionsUiState.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class TransactionsScreenViewModel @Inject constructor(
    transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TransactionsUiState>(Loading)
    val uiState: StateFlow<TransactionsUiState>
        get() = _uiState

    init {
        transactionRepository.getTransactions()
            .flowOn(Dispatchers.IO)
            .onEach {
                _uiState.value = Success(convertToInfo(it))
            }
            .launchIn(viewModelScope)
    }

    private fun convertToInfo(transactions: List<TransactionEntity>): TransactionsScreenInfo {
        val daily = transactions
            .groupBy {
                Instant.ofEpochMilli(it.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .format(DateTimeFormatter.ISO_DATE)
            }
            .map { (date, transactions) ->
                TransactionsDaily(
                    date = date,
                    summary = transactions
                        .groupBy { it.categoryType }
                        .mapValues { (_, list) -> list.sumOf { it.amount } }
                        .let {
                            val income = it[CategoryType.INCOME] ?: 0
                            val expense = it[CategoryType.EXPENSE] ?: 0
                            TransactionsSummary(income, expense, income - expense)
                        },
                    items = transactions.map {
                        val amountText = it.amount.toString()
                        TransactionsDailyItem(
                            id = it.id,
                            category = when (it.categoryType) {
                                CategoryType.EXPENSE -> ExpenseCategory.valueOf(it.categoryKey)
                                CategoryType.INCOME -> IncomeCategory.valueOf(it.categoryKey)
                            },
                            note = it.note,
                            amountText = when (it.categoryType) {
                                CategoryType.INCOME -> amountText
                                CategoryType.EXPENSE -> "-$amountText"
                            }
                        )
                    }
                )
            }

        val summary = daily.fold(TransactionsSummary(0, 0, 0)) { acc, day ->
            val income = acc.income + day.summary.income
            val expense = acc.expense + day.summary.expense
            TransactionsSummary(
                income = income,
                expense = expense,
                balance = income - expense
            )
        }

        return TransactionsScreenInfo(
            summary = summary,
            daily = daily
        )
    }
}

sealed class TransactionsUiState {
    data object Loading : TransactionsUiState()
    class Success(val info: TransactionsScreenInfo) : TransactionsUiState()
}