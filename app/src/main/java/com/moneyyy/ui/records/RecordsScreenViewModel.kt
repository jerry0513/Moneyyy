package com.moneyyy.ui.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moneyyy.data.price.PriceType
import com.moneyyy.data.database.RecordEntity
import com.moneyyy.data.RecordRepository
import com.moneyyy.ui.records.RecordsUiState.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RecordsScreenViewModel(
    recordRepository: RecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecordsUiState>(Loading)
    val uiState: StateFlow<RecordsUiState>
        get() = _uiState

    init {
        recordRepository.getRecords()
            .flowOn(Dispatchers.IO)
            .onEach {
                _uiState.value = Success(convertToInfo(it))
            }
            .launchIn(viewModelScope)
    }

    private fun convertToInfo(records: List<RecordEntity>): RecordsScreenInfo {
        val sectionSummary = records.groupBy {
            it.priceInfo.type
        }
            .let { typeToRecords ->
                val incomeSum = typeToRecords[PriceType.INCOME]?.sumOf {
                    it.priceInfo.price
                } ?: 0
                val expenseSum = typeToRecords[PriceType.EXPENSE]?.sumOf {
                    it.priceInfo.price
                } ?: 0
                RecordsSectionSummary(
                    incomeSum = incomeSum,
                    expenseSum = expenseSum,
                    balance = incomeSum - expenseSum
                )
            }

        val sectionItems = records.groupBy {
            LocalDate.ofInstant(Instant.ofEpochMilli(it.date), ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_DATE)
        }
            .map { (date, records) ->
                RecordsSectionDailyInfo(
                    date = date,
                    balance = records.groupBy {
                        it.priceInfo.type
                    }
                        .let { typeToRecords ->
                            val incomeSum = typeToRecords[PriceType.INCOME]?.sumOf {
                                it.priceInfo.price
                            } ?: 0
                            val expenseSum = typeToRecords[PriceType.EXPENSE]?.sumOf {
                                it.priceInfo.price
                            } ?: 0
                            (incomeSum - expenseSum)
                        },
                    items = records.map {
                        val priceText = it.priceInfo.price.toString()
                        RecordsSectionDailyItem(
                            id = it.id,
                            category = it.category,
                            note = it.note,
                            priceText = when (it.priceInfo.type) {
                                PriceType.INCOME -> priceText
                                PriceType.EXPENSE -> "-$priceText"
                            }
                        )
                    }
                )
            }

        return RecordsScreenInfo(
            summary = sectionSummary,
            dailyInfo = sectionItems
        )
    }

    companion object {
        fun provideFactory(
            recordRepository: RecordRepository
        ): ViewModelProvider.NewInstanceFactory {
            return object : ViewModelProvider.NewInstanceFactory() {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RecordsScreenViewModel(recordRepository) as T
                }
            }
        }
    }
}

sealed class RecordsUiState {
    data object Loading : RecordsUiState()
    class Success(val info: RecordsScreenInfo) : RecordsUiState()
}