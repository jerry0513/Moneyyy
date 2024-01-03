package com.moneyyy.data

import com.moneyyy.data.database.AppDatabase
import com.moneyyy.data.database.RecordDao
import com.moneyyy.data.database.RecordEntity
import kotlinx.coroutines.flow.Flow

class RecordRepository(
    private val recordDao: RecordDao = AppDatabase.getInstance().userDao()
) {
    fun getRecords(): Flow<List<RecordEntity>> = recordDao.getAll()

    fun getRecord(id: Int): Flow<RecordEntity?> = recordDao.get(id)

    suspend fun updateRecord(record: RecordEntity) = recordDao.update(record)

    suspend fun addRecord(record: RecordEntity) = recordDao.add(record)

    suspend fun deleteRecord(id: Int) = recordDao.delete(id)
}