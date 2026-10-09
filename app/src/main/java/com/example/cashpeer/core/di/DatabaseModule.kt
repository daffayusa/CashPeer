package com.example.cashpeer.core.di

import android.content.Context
import androidx.room3.Room
import com.example.cashpeer.core.database.AppDatabase
import com.example.cashpeer.core.database.MIGRATION_1_2
import com.example.cashpeer.feature.category.data.local.CategoryDao
import com.example.cashpeer.feature.transaction.data.local.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun ProvideAppDatabase(
        @ApplicationContext contex: Context
    ): AppDatabase{
        return Room.databaseBuilder(
            contex,
            AppDatabase::class.java,
            "cashpeer_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    fun ProvideTransactionDao(
        database: AppDatabase
    ): TransactionDao{
        return database.transactionDao()
    }

    @Provides
    fun provideCategoryDao(
        database: AppDatabase
    ): CategoryDao = database.categoryDao()
}