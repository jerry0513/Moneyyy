package com.moneyyy.data.repository

import com.moneyyy.data.database.TransactionDao
import com.moneyyy.data.database.TransactionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class DefaultTransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {
    override fun observeTransactionsByYearMonth(
        year: Int,
        month: Int
    ): Flow<List<TransactionEntity>> {
        val startOfMonth = LocalDate.of(year, month, 1).atStartOfDay(ZoneId.systemDefault())
        val endOfMonth = startOfMonth.plusMonths(1)

        val startMillis = startOfMonth.toInstant().toEpochMilli()
        val endMillis = endOfMonth.toInstant().toEpochMilli()

        return transactionDao.getAllByYearMonth(startMillis, endMillis)
    }

    override fun observeTransaction(id: Int): Flow<TransactionEntity?> {
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