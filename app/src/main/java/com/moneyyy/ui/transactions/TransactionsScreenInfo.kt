package com.moneyyy.ui.transactions

import com.moneyyy.data.model.Category

data class TransactionsScreenInfo(
    val summary: TransactionsSummary,
    val daily: List<TransactionsDaily>
)

data class TransactionsSummary(
    val income: Int,
    val expense: Int,
    val balance: Int,
)

data class TransactionsDaily(
    val date: String,
    val summary: TransactionsSummary,
    val items: List<TransactionsDailyItem>,
)

data class TransactionsDailyItem(
    val id: Int,
    val category: Category,
    val note: String,
    val amountText: String,
)