package com.moneyyy.ui.transactionedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneyyy.data.Millis
import com.moneyyy.data.database.TransactionEntity
import com.moneyyy.data.model.Category
import com.moneyyy.data.model.CategoryType
import com.moneyyy.data.model.ExpenseCategory
import com.moneyyy.data.model.IncomeCategory
import com.moneyyy.data.repository.DefaultTransactionRepository
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@HiltViewModel(assistedFactory = TransactionEditViewModel.Factory::class)
class TransactionEditViewModel @AssistedInject constructor(
    @Assisted private val id: Int?,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val transactionRepository: DefaultTransactionRepository
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(id: Int?): TransactionEditViewModel
    }

    private val _uiState = MutableStateFlow(TransactionEditUiState())
    val uiState: StateFlow<TransactionEditUiState>
        get() = _uiState

    private val _uiEvent = Channel<TransactionEditUiEvent>()
    val uiEvent: Flow<TransactionEditUiEvent>
        get() = _uiEvent.receiveAsFlow()

    private val isUpdateTransaction = id != null

    init {
        if (isUpdateTransaction) {
            transactionRepository.observeTransaction(id!!)
                .filterNotNull()
                .map {
                    TransactionEditUiState(
                        category = when (it.categoryType) {
                            CategoryType.EXPENSE -> ExpenseCategory.valueOf(it.categoryKey)
                            CategoryType.INCOME -> IncomeCategory.valueOf(it.categoryKey)
                        },
                        date = Instant.ofEpochMilli(it.timestamp)
                            .atZone(ZoneId.systemDefault())
                            .convertToDateModel(),
                        note = it.note,
                        amountText = it.amount.toString(),
                        categoryType = it.categoryType
                    )
                }
                .flowOn(ioDispatcher)
                .onStart {
                    emit(TransactionEditUiState(isLoading = true))
                }
                .onEach { _uiState.value = it }
                .launchIn(viewModelScope)
        } else {
            _uiState.value = TransactionEditUiState(
                date = ZonedDateTime.now()
                    .convertToDateModel()
            )
        }
    }

    fun setType(categoryType: CategoryType) {
        if (categoryType == _uiState.value.categoryType) return

        _uiState.value = _uiState.value.copy(
            category = null,
            categoryType = categoryType
        )
    }

    fun setCategory(category: Category) {
        if (category == _uiState.value.category) return

        _uiState.value = _uiState.value.copy(category = category)
    }

    fun setNote(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun setDate(dateMillis: Millis) {
        if (dateMillis == _uiState.value.date?.correctedTimestamp) return

        _uiState.value = _uiState.value.copy(
            date = Instant.ofEpochMilli(dateMillis)
                .atZone(ZoneId.systemDefault())
                .convertToDateModel()
        )
    }

    fun setNumber(number: String) {
        val currentAmount = _uiState.value.amountText
        if (currentAmount.length + number.length > 9) return

        val newAmount = if (currentAmount == "0") {
            number
        } else {
            currentAmount + number
        }
        _uiState.value = _uiState.value.copy(amountText = newAmount)
    }

    fun backspaceNumber() {
        val currentAmount = _uiState.value.amountText
        val newAmount = if (currentAmount.length == 1) {
            "0"
        } else {
            currentAmount.dropLast(1)
        }
        _uiState.value = _uiState.value.copy(amountText = newAmount)
    }

    fun confirm() {
        if (isInvalidEdit()) return

        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val transaction = TransactionEntity(
                id = if (isUpdateTransaction) id!! else 0,
                timestamp = _uiState.value.date!!.timestamp,
                categoryKey = _uiState.value.category!!.name,
                categoryType = uiState.value.categoryType,
                note = _uiState.value.note,
                amount = _uiState.value.amountText.toInt()
            )
            withContext(ioDispatcher) {
                if (isUpdateTransaction) {
                    transactionRepository.updateTransaction(transaction)
                } else {
                    transactionRepository.addTransaction(transaction)
                }
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
            _uiEvent.send(TransactionEditUiEvent.EditSuccess)
        }
    }

    private fun isInvalidEdit(): Boolean {
        if (_uiState.value.amountText == "0") {
            _uiEvent.trySend(TransactionEditUiEvent.InvalidAmount)
            return true
        }

        return false
    }

    private fun ZonedDateTime.convertToDateModel(): TransactionEditDate {
        val millis = this.toInstant().toEpochMilli()

        return TransactionEditDate(
            year = this.year,
            month = this.monthValue,
            dayOfMonth = this.dayOfMonth,
            timestamp = millis,
            correctedTimestamp = millis + offset.totalSeconds * 1000
        )
    }
}

data class TransactionEditUiState(
    val isLoading: Boolean = false,
    val category: Category? = null,
    val date: TransactionEditDate? = null,
    val note: String = "",
    val amountText: String = "0",
    val categoryType: CategoryType = CategoryType.EXPENSE
)

data class TransactionEditDate(
    val year: Int,
    val month: Int,
    val dayOfMonth: Int,
    val timestamp: Millis,
    val correctedTimestamp: Millis
)

sealed class TransactionEditUiEvent {
    data object EditSuccess : TransactionEditUiEvent()
    data object InvalidAmount : TransactionEditUiEvent()
}