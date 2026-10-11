package com.example.cashpeer.core.di

import com.example.cashpeer.feature.savinggoal.domain.repository.SavingGoalRepository
import com.example.cashpeer.feature.savinggoal.data.repository.SavingGoalRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SavingGoalModule {
    @Binds
    @Singleton
    abstract fun bindSavingGoalRepository(
        impl: SavingGoalRepositoryImpl
    ): SavingGoalRepository
}