package com.moneyyy.ui.transaction

import com.moneyyy.data.model.Category
import com.moneyyy.data.model.CategoryType

data class TransactionScreenInfo(
    val category: Category,
    val categoryType: CategoryType,
    val amount: Int,
    val date: String,
    val note: String
)