@file:Suppress("unused")

package com.moneyyy.di

import android.content.Context
import androidx.room.Room
import com.moneyyy.data.database.MoneyyyDatabase
import com.moneyyy.data.database.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideMoneyyyDatabase(@ApplicationContext context: Context): MoneyyyDatabase {
        return Room.databaseBuilder(
            context,
            MoneyyyDatabase::class.java,
            "moneyyy_database"
        )
            .build()
    }

    @Provides
    fun provideTransactionDao(database: MoneyyyDatabase): TransactionDao {
        return database.transactionDao()
    }
}