package com.moneyyy.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactionentity ORDER BY timestamp DESC")
    fun getAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactionentity WHERE id = :id")
    fun get(id: Int): Flow<TransactionEntity?>

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(transaction: TransactionEntity)

    @Query("DELETE FROM transactionentity WHERE id = :id")
    suspend fun delete(id: Int)
}