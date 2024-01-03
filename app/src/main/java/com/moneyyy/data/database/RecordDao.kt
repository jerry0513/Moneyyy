package com.moneyyy.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Query("SELECT * FROM recordentity ORDER BY date DESC")
    fun getAll(): Flow<List<RecordEntity>>

    @Query("SELECT * FROM recordentity WHERE id = :id")
    fun get(id: Int): Flow<RecordEntity>

    @Update
    suspend fun update(record: RecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(record: RecordEntity)

    @Query("DELETE FROM recordentity WHERE id = :id")
    suspend fun delete(id: Int)
}