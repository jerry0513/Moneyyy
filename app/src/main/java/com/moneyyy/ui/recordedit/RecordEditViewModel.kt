package com.moneyyy.ui.recordedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moneyyy.data.Millis
import com.moneyyy.data.price.PriceType
import com.moneyyy.data.database.RecordEntity
import com.moneyyy.data.database.RecordPriceInfoEntity
import com.moneyyy.data.RecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class RecordEditViewModel(
    private val id: Int?,
    private val recordRepository: RecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordEditUiState())
    val uiState: StateFlow<RecordEditUiState>
        get() = _uiState

    private val _onEditingSuccessful = Channel<Unit>()
    val onEditingSuccessful: Flow<Unit>
        get() = _onEditingSuccessful.receiveAsFlow()

    private lateinit var date: ZonedDateTime

    private val isUpdateRecord = id != null

    init {
        if (isUpdateRecord) {
            _uiState.value = RecordEditUiState(showLoadingView = true)
            recordRepository.getRecord(id!!)
                .flowOn(Dispatchers.IO)
                .filterNotNull()
                .onEach {
                    date = Instant.ofEpochMilli(it.date)
                        .atZone(ZoneId.systemDefault())
                    _uiState.value = RecordEditUiState(
                        category = it.category,
                        date = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        note = it.note,
                        price = it.priceInfo.price.toString()
                    )
                }
                .launchIn(viewModelScope)
        } else {
            date = ZonedDateTime.now()
            _uiState.value = RecordEditUiState(
                date = date.format(DateTimeFormatter.ISO_LOCAL_DATE),
            )
        }
    }

    fun setCategory(category: String) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun setNote(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun setDate(date: Millis) {
        val localDate = Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .also { this.date = it }
        val dateString = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        _uiState.value = _uiState.value.copy(date = dateString)
    }

    fun setNumber(number: String) {
        val currentPrice = _uiState.value.price
        if (currentPrice.length + number.length > 9) {
            return
        }

        val newPrice = if (currentPrice == "0") {
            number
        } else {
            currentPrice + number
        }
        _uiState.value = _uiState.value.copy(price = newPrice)
    }

    fun removeNumber() {
        val currentPrice = _uiState.value.price
        val newPrice = if (currentPrice.length == 1) {
            "0"
        } else {
            currentPrice.dropLast(1)
        }
        _uiState.value = _uiState.value.copy(price = newPrice)
    }

    fun confirmRecord() {
        _uiState.value = _uiState.value.copy(showLoadingDialog = true)
        viewModelScope.launch {
            val record = RecordEntity(
                id = if (isUpdateRecord) id!! else 0,
                date = date.toInstant().toEpochMilli(),
                category = _uiState.value.category,
                note = _uiState.value.note,
                priceInfo = RecordPriceInfoEntity(
                    type = PriceType.EXPENSE,
                    price = _uiState.value.price.toInt()
                )
            )
            withContext(Dispatchers.IO) {
                if (isUpdateRecord) {
                    recordRepository.updateRecord(record)
                } else {
                    recordRepository.addRecord(record)
                }
            }
            _uiState.value = _uiState.value.copy(showLoadingDialog = false)
            _onEditingSuccessful.send(Unit)
        }
    }

    companion object {
        fun provideFactory(
            id: Int?,
            recordRepository: RecordRepository
        ): ViewModelProvider.NewInstanceFactory {
            return object : ViewModelProvider.NewInstanceFactory() {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RecordEditViewModel(
                        id,
                        recordRepository
                    ) as T
                }
            }
        }
    }
}

data class RecordEditUiState(
    val showLoadingView: Boolean = false,
    val showLoadingDialog: Boolean = false,
    val category: String = "",
    val date: String = "",
    val note: String = "",
    val price: String = ""
)