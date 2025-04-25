package com.moneyyy.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneyyy.data.database.TransactionEntity
import com.moneyyy.data.model.CategoryType
import com.moneyyy.data.model.ExpenseCategory
import com.moneyyy.data.model.IncomeCategory
import com.moneyyy.data.repository.TransactionRepository
import com.moneyyy.di.IoDispatcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@HiltViewModel(assistedFactory = TransactionViewModel.Factory::class)
class TransactionViewModel @AssistedInject constructor(
    @Assisted private val id: Int,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(id: Int): TransactionViewModel
    }

    private val _uiState = MutableStateFlow(TransactionUiState(isInitialLoading = true))
    val uiState: StateFlow<TransactionUiState>
        get() = _uiState

    private val _uiEvent = Channel<TransactionUiEvent>()
    val uiEvent: Flow<TransactionUiEvent>
        get() = _uiEvent.receiveAsFlow()

    init {
        transactionRepository.observeTransaction(id)
            .filterNotNull()
            .map { TransactionUiState(info = convertToInfo(it)) }
            .flowOn(ioDispatcher)
            .onEach { _uiState.value = it }
            .launchIn(viewModelScope)
    }

    fun delete() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            withContext(ioDispatcher) {
                transactionRepository.deleteTransaction(id)
            }
            _uiState.update { it.copy(isDeleting = false) }
            _uiEvent.send(TransactionUiEvent.DeleteSuccess)
        }
    }

    private fun convertToInfo(transaction: TransactionEntity): TransactionScreenInfo {
        return TransactionScreenInfo(
            category = when (transaction.categoryType) {
                CategoryType.EXPENSE -> ExpenseCategory.valueOf(transaction.categoryKey)
                CategoryType.INCOME -> IncomeCategory.valueOf(transaction.categoryKey)
            },
            categoryType = transaction.categoryType,
            amount = transaction.amount,
            date = LocalDate.ofInstant(
                Instant.ofEpochMilli(transaction.timestamp),
                ZoneId.systemDefault()
            )
                .format(DateTimeFormatter.ISO_LOCAL_DATE),
            note = transaction.note,
        )
    }
}

data class TransactionUiState(
    val isInitialLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val info: TransactionScreenInfo? = null
)

sealed class TransactionUiEvent {
    data object DeleteSuccess: TransactionUiEvent()
}