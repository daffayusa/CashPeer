package com.example.cashpeer.core.di

import android.content.Context
import androidx.room3.Room
import com.example.cashpeer.core.database.AppDatabase
import com.example.cashpeer.feature.transaction.data.repository.TransactionRepositoryImpl
import com.example.cashpeer.feature.transaction.domain.repository.TransactionRepository
import com.example.cashpeer.feature.transaction.domain.usecase.AddTransactionUseCase
import com.example.cashpeer.feature.transaction.domain.usecase.DeleteTransactionUseCase
import com.example.cashpeer.feature.transaction.domain.usecase.GetTransactionByIdUseCase
import com.example.cashpeer.feature.transaction.domain.usecase.GetTransactionsUseCase
import com.example.cashpeer.feature.transaction.domain.usecase.UpdateTransactionUseCase

class AppContainer(
    context: Context
) {

    private val database: AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cashpeer_database"
        )
            .build()

    private val transactionRepository: TransactionRepository =
        TransactionRepositoryImpl(
            transactionDao = database.transactionDao()
        )

    val getTransactionsUseCase =
        GetTransactionsUseCase(
            repository = transactionRepository
        )

    val getTransactionByIdUseCase =
        GetTransactionByIdUseCase(
            repository = transactionRepository
        )

    val addTransactionUseCase =
        AddTransactionUseCase(
            repository = transactionRepository
        )

    val updateTransactionUseCase =
        UpdateTransactionUseCase(
            repository = transactionRepository
        )

    val deleteTransactionUseCase =
        DeleteTransactionUseCase(
            repository = transactionRepository
        )
}