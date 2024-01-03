package com.moneyyy.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RecordEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    @TypeConverters(PriceTypeConverter::class)
    abstract fun userDao(): RecordDao

    companion object {
        private var context: Context? = null

        @Volatile
        private var instance: AppDatabase? = null

        fun init(context: Context) {
            Companion.context = context
        }

        fun getInstance(): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context!!,
                    AppDatabase::class.java,
                    "moneyyy.db"
                )
                    .build()
                    .also { instance = it }
            }
        }
    }
}