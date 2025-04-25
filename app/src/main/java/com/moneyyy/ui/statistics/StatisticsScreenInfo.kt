package com.moneyyy.ui.statistics

import com.moneyyy.data.model.Category
import com.moneyyy.data.model.CategoryType

data class StatisticsScreenInfo(
    val date: StatisticsDate,
    val categoryType: CategoryType,
    val amount: Int,
    val topCategoryItems: List<StatisticsCategoryItem>,
    val categoryItems: List<StatisticsCategoryItem>
)

data class StatisticsDate(
    val year: Int,
    val month: Int
)

data class StatisticsCategoryItem(
    val category: Category?,
    val amount: Int,
    val percentageValue: Float
)
