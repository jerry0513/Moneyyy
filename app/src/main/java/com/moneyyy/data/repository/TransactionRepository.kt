package com.moneyyy.data.repository

import com.moneyyy.data.database.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getTransactions(): Flow<List<TransactionEntity>>
    fun getTransaction(id: Int): Flow<TransactionEntity?>
    suspend fun updateTransaction(transaction: TransactionEntity)
    suspend fun addTransaction(transaction: TransactionEntity)
    suspend fun deleteTransaction(id: Int)
}