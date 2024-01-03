package com.moneyyy.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moneyyy.data.price.PriceInfo
import com.moneyyy.data.database.RecordEntity
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
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RecordScreenViewModel(
    private val id: Int,
    private val recordRepository: RecordRepository = RecordRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordUiState(showLoadingView = true))
    val uiState: StateFlow<RecordUiState>
        get() = _uiState

    private val _onDeleteSuccessful = Channel<Unit>()
    val onDeleteSuccessful: Flow<Unit>
        get() = _onDeleteSuccessful.receiveAsFlow()

    init {
        recordRepository.getRecord(id)
            .flowOn(Dispatchers.IO)
            .filterNotNull()
            .onEach {
                _uiState.value = RecordUiState(info = convertToInfo(it))
            }
            .launchIn(viewModelScope)
    }

    fun deleteRecord() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(showLoadingDialog = true)
            withContext(Dispatchers.IO) {
                recordRepository.deleteRecord(id)
            }
            _uiState.value = uiState.value.copy(showLoadingDialog = false)
            _onDeleteSuccessful.send(Unit)
        }
    }

    private fun convertToInfo(record: RecordEntity): RecordScreenInfo {
        return RecordScreenInfo(
            category = record.category,
            priceInfo = PriceInfo(
                type = record.priceInfo.type,
                price = record.priceInfo.price
            ),
            date = LocalDate.ofInstant(Instant.ofEpochMilli(record.date), ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_LOCAL_DATE),
            note = record.note,
        )
    }

    companion object {
        fun provideFactory(
            id: Int,
            recordRepository: RecordRepository
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RecordScreenViewModel(
                        id,
                        recordRepository
                    ) as T
                }
            }
        }
    }
}

data class RecordUiState(
    val showLoadingView: Boolean = false,
    val showLoadingDialog: Boolean = false,
    val info: RecordScreenInfo? = null
)