package com.moneyyy.data.repository

import com.moneyyy.data.database.TransactionDao
import com.moneyyy.data.database.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultTransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
): TransactionRepository {
    override fun getTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAll()
    }

    override fun getTransaction(id: Int): Flow<TransactionEntity?> {
        return transactionDao.get(id)
    }

    override suspend fun updateTransaction(transaction: TransactionEntity) {
        return transactionDao.update(transaction)
    }

    override suspend fun addTransaction(transaction: TransactionEntity) {
        return transactionDao.add(transaction)
    }

    override suspend fun deleteTransaction(id: Int) {
        return transactionDao.delete(id)
    }
}