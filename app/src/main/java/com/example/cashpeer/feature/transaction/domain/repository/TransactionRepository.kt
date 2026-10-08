package com.example.cashpeer.feature.transaction.domain.repository

import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository{
    fun observeTransactions(
        userId: Long
    ): Flow<List<Transaction>>

    fun observeTransactionsByType(
        userId: Long,
        type: TransactionType
    ): Flow<List<Transaction>>

    fun observeRecentTransactions(
        userId: Long,
        limit: Int = 5
    ): Flow<List<Transaction>>

    fun observeTotalIncome(
        userId: Long
    ): Flow<Long>

    fun observeTotalExpense(
        userId: Long
    ): Flow<Long>

    fun observeTotalSaving(
        userId: Long
    ): Flow<Long>

    fun observeTotalByTypeAndDateRange(
        userId: Long,
        type: TransactionType,
        startDate: String,
        endDate: String
    ): Flow<Long>

    suspend fun getTransactionById(
        userId: Long,
        transactionId: Long
    ): Transaction?

    suspend fun insert(
        transaction: Transaction
    ): Long

    suspend fun update(
        transaction: Transaction
    )

    suspend fun delete(
        transaction: Transaction
    )
}