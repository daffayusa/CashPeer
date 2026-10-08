package com.example.cashpeer.core.di

import com.example.cashpeer.feature.transaction.data.repository.TransactionRepositoryImpl
import com.example.cashpeer.feature.transaction.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule{
    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        implementation: TransactionRepositoryImpl
    ): TransactionRepository
}