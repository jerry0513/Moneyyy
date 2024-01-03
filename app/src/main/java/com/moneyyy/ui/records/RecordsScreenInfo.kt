package com.moneyyy.ui.records

data class RecordsScreenInfo(
    val summary: RecordsSectionSummary,
    val dailyInfo: List<RecordsSectionDailyInfo>
)

data class RecordsSectionSummary(
    val incomeSum: Int,
    val expenseSum: Int,
    val balance: Int,
)

data class RecordsSectionDailyInfo(
    val date: String,
    val balance: Int,
    val items: List<RecordsSectionDailyItem>,
)

data class RecordsSectionDailyItem(
    val id: Int,
    val category: String,
    val note: String,
    val priceText: String,
)
