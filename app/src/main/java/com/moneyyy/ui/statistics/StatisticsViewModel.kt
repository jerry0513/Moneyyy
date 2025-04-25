package com.moneyyy.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moneyyy.data.database.TransactionEntity
import com.moneyyy.data.model.CategoryType
import com.moneyyy.data.model.ExpenseCategory
import com.moneyyy.data.model.IncomeCategory
import com.moneyyy.data.repository.TransactionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@HiltViewModel(assistedFactory = StatisticsViewModel.Factory::class)
class StatisticsViewModel @AssistedInject constructor(
    @Assisted("initialYear") private val initialYear: Int,
    @Assisted("initialMonth") private val initialMonth: Int,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("initialYear") initialYear: Int,
            @Assisted("initialMonth") initialMonth: Int
        ): StatisticsViewModel
    }

    private val _uiState = MutableStateFlow<StatisticsUiState>(StatisticsUiState.Loading)
    val uiState: StateFlow<StatisticsUiState>
        get() = _uiState

    private val yearMonthFlow: MutableStateFlow<StatisticsDate> = MutableStateFlow(
        StatisticsDate(year = initialYear, month = initialMonth)
    )

    private var cache: Map<CategoryType, StatisticsScreenInfo> = mapOf()

    init {
        yearMonthFlow
            .flatMapLatest { date ->
                transactionRepository.observeTransactionsByYearMonth(date.year, date.month)
                    .flowOn(Dispatchers.IO)
                    .map { convertToInfo(date, it) }
            }
            .onEach {
                cache = it
                _uiState.value = StatisticsUiState.Success(it[CategoryType.EXPENSE]!!)
            }
            .launchIn(viewModelScope)
    }

    fun setDate(year: Int, month: Int) {
        yearMonthFlow.value = StatisticsDate(year = year, month = month)
    }

    fun swapType() {
        when (val value = _uiState.value) {
            is StatisticsUiState.Success -> {
                val nextType = if (value.info.categoryType == CategoryType.EXPENSE) {
                    CategoryType.INCOME
                } else {
                    CategoryType.EXPENSE
                }
                _uiState.value = StatisticsUiState.Success(cache[nextType]!!)
            }

            else -> {}
        }
    }

    private fun convertToInfo(
        date: StatisticsDate,
        entities: List<TransactionEntity>
    ): Map<CategoryType, StatisticsScreenInfo> {
        val groupedByType = entities.groupBy { it.categoryType }

        return CategoryType.entries.associateWith { type ->
            val entitiesInType = groupedByType[type].orEmpty()

            val categoryEntities = entitiesInType.groupBy { entity ->
                when (type) {
                    CategoryType.EXPENSE -> ExpenseCategory.valueOf(entity.categoryKey)
                    CategoryType.INCOME -> IncomeCategory.valueOf(entity.categoryKey)
                }
            }

            val totalAmount = entitiesInType.sumOf { it.amount }

            val sortedItems = categoryEntities.map { (category, items) ->
                val sum = items.sumOf { it.amount }
                val percentage = sum * 100f / totalAmount
                StatisticsCategoryItem(
                    category = category,
                    amount = sum,
                    percentageValue = percentage
                )
            }.sortedByDescending { it.amount }

            val topItems = if (sortedItems.size > 5) {
                val top4 = sortedItems.take(4)
                val others = sortedItems.drop(4)
                val otherAmount = others.sumOf { it.amount }
                val otherPercentage = others.sumOf { it.percentageValue.toDouble() }.toFloat()
                top4 + StatisticsCategoryItem(null, otherAmount, otherPercentage)
            } else {
                sortedItems
            }

            StatisticsScreenInfo(
                date = date,
                categoryType = type,
                amount = totalAmount,
                topCategoryItems = topItems,
                categoryItems = sortedItems
            )
        }
    }
}

sealed class StatisticsUiState {
    data object Loading : StatisticsUiState()

    data class Success(
        val info: StatisticsScreenInfo
    ) : StatisticsUiState()
}