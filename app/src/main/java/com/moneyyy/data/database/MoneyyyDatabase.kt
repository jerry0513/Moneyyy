package com.moneyyy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [TransactionEntity::class],
    version = 1
)
abstract class MoneyyyDatabase : RoomDatabase() {
    @TypeConverters(CategoryTypeConverter::class)
    abstract fun transactionDao(): TransactionDao
}