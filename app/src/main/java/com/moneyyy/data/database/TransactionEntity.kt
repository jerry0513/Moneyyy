package com.moneyyy.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.moneyyy.data.Millis
import com.moneyyy.data.model.CategoryType

@Entity
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "amount") val amount: Int,
    @ColumnInfo(name = "category_key") val categoryKey: String,
    @ColumnInfo(name = "category_type") val categoryType: CategoryType,
    @ColumnInfo(name = "note") val note: String,
    @ColumnInfo(name = "timestamp") val timestamp: Millis
)