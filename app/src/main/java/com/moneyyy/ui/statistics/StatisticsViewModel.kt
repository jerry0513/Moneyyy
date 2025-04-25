package com.moneyyy.ui.statistics

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = StatisticsViewModel.Factory::class)
class StatisticsViewModel @AssistedInject constructor(
    @Assisted("initialYear") private val initialYear: Int,
    @Assisted("initialMonth") private val initialMonth: Int,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("initialYear") initialYear: Int,
            @Assisted("initialMonth") initialMonth: Int
        ): StatisticsViewModel
    }

    private val _uiState = MutableStateFlow<StatisticsUiState>(
        createEmptyUiState(StatisticsDate(year = initialYear, month = initialMonth), true)
    )
    val uiState: StateFlow<StatisticsUiState>
        get() = _uiState

    private val dateFlow: MutableStateFlow<StatisticsDate> =
        MutableStateFlow(_uiState.value.info.date)

    private var cache: Map<CategoryType, StatisticsScreenInfo> = mapOf()

    init {
        dateFlow
            .flatMapLatest { date ->
                transactionRepository.observeTransactionsByYearMonth(date.year, date.month)
                    .map { transactions ->
                        val info = convertToInfo(date, transactions).also { cache = it }
                        StatisticsUiState(info[CategoryType.EXPENSE]!!)
                    }
                    .flowOn(ioDispatcher)
                    .onStart {
                        emit(
                            StatisticsUiState(
                                info = _uiState.value.info.copy(date = date),
                                isLoading = true
                            )
                        )
                    }
                    .catch { throwable ->
                        cache = mapOf()
                        emit(createEmptyUiState(date, false, throwable.toString()))
                    }
            }
            .onEach { _uiState.value = it }
            .launchIn(viewModelScope)
    }

    fun setDate(year: Int, month: Int) {
        dateFlow.value = StatisticsDate(year = year, month = month)
    }

    fun swapType() {
        if (cache.isEmpty()) return

        _uiState.update {
            val nextType = if (it.info.categoryType == CategoryType.EXPENSE) {
                CategoryType.INCOME
            } else {
                CategoryType.EXPENSE
            }
            StatisticsUiState(cache[nextType]!!)
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

    private fun createEmptyUiState(
        date: StatisticsDate,
        isLoading: Boolean,
        errorMessage: String = ""
    ): StatisticsUiState {
        return StatisticsUiState(
            info = StatisticsScreenInfo(
                date = date,
                categoryType = CategoryType.EXPENSE,
                amount = 0,
                topCategoryItems = listOf(),
                categoryItems = listOf()
            ),
            isLoading = isLoading,
            errorMessage = errorMessage
        )
    }
}

data class StatisticsUiState(
    val info: StatisticsScreenInfo,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)