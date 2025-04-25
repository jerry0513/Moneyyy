package com.moneyyy.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.moneyyy.data.Millis
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query(
        """
        SELECT * FROM transactionentity
        WHERE timestamp >= :startUtc
            AND timestamp < :endUtc
        ORDER BY timestamp DESC
        """
    )
    fun getAllByYearMonth(startUtc: Millis, endUtc: Millis): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactionentity WHERE id = :id")
    fun get(id: Int): Flow<TransactionEntity?>

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(transaction: TransactionEntity)

    @Query("DELETE FROM transactionentity WHERE id = :id")
    suspend fun delete(id: Int)
}