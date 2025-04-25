package com.moneyyy.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneyyy.data.database.TransactionEntity
import com.moneyyy.data.model.CategoryType
import com.moneyyy.data.model.ExpenseCategory
import com.moneyyy.data.model.IncomeCategory
import com.moneyyy.data.repository.TransactionRepository
import com.moneyyy.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        createEmptyUiState(
            LocalDate.now().let {
                TransactionsDate(year = it.year, month = it.monthValue)
            },
            true
        )
    )
    val uiState: StateFlow<TransactionsUiState>
        get() = _uiState

    private val _uiEvent = Channel<TransactionsUiEvent>()
    val uiEvent: Flow<TransactionsUiEvent>
        get() = _uiEvent.receiveAsFlow()

    private val dateFlow = MutableStateFlow(_uiState.value.info.date)

    init {
        dateFlow
            .flatMapLatest { date ->
                transactionRepository.observeTransactionsByYearMonth(date.year, date.month)
                    .map { transactions ->
                        val info = convertToInfo(date, transactions)
                        TransactionsUiState(info)
                    }
                    .flowOn(ioDispatcher)
                    .onStart {
                        emit(
                            TransactionsUiState(
                                info = _uiState.value.info.copy(date = date),
                                isLoading = true
                            )
                        )
                    }
                    .catch { throwable ->
                        emit(createEmptyUiState(date, false, throwable.toString()))
                    }
            }
            .onEach { _uiState.value = it }
            .launchIn(viewModelScope)
    }

    fun clickStatistics() {
        viewModelScope.launch {
            val date = _uiState.value.info.date
            _uiEvent.send(TransactionsUiEvent.NavigateToStatistics(date.year, date.month))
        }
    }

    fun setDate(year: Int, month: Int) {
        dateFlow.value = TransactionsDate(year, month)
    }

    private fun convertToInfo(
        date: TransactionsDate,
        transactions: List<TransactionEntity>
    ): TransactionsScreenInfo {
        val daily = transactions
            .groupBy {
                LocalDate.ofInstant(
                    Instant.ofEpochMilli(it.timestamp),
                    ZoneId.systemDefault()
                )
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
            date = date,
            summary = summary,
            daily = daily
        )
    }

    private fun createEmptyUiState(
        date: TransactionsDate,
        isLoading: Boolean,
        errorMessage: String = ""
    ): TransactionsUiState {
        return TransactionsUiState(
            info = TransactionsScreenInfo(
                date = date,
                summary = TransactionsSummary(income = 0, expense = 0, balance = 0),
                daily = listOf()
            ),
            isLoading = isLoading,
            errorMessage = errorMessage
        )
    }
}

data class TransactionsUiState(
    val info: TransactionsScreenInfo,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

sealed class TransactionsUiEvent {
    data class NavigateToStatistics(
        val year: Int,
        val month: Int
    ) : TransactionsUiEvent()
}