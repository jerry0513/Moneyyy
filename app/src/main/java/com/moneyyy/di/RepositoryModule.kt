@file:Suppress("unused")

package com.moneyyy.di

import com.moneyyy.data.repository.DefaultTransactionRepository
import com.moneyyy.data.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindTransactionRepository(
        transactionRepository: DefaultTransactionRepository
    ): TransactionRepository
}