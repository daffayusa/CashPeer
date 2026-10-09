package com.example.cashpeer.feature.transaction.data.repository

import com.example.cashpeer.feature.transaction.data.local.TransactionDao
import com.example.cashpeer.feature.category.data.mapper.toDomain
import com.example.cashpeer.feature.transaction.data.mapper.toDomain
import com.example.cashpeer.feature.transaction.data.mapper.toEntity
import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import com.example.cashpeer.feature.transaction.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {

    override fun observeTransactions(
        userId: Long
    ): Flow<List<Transaction>> {
        return transactionDao
            .observeTransactions(userId)
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override fun observeTransactionsByType(
        userId: Long,
        type: TransactionType
    ): Flow<List<Transaction>> {
        return transactionDao
            .observeTransactionByType(
                userId = userId,
                type = type.name
            )
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override fun observeRecentTransactions(
        userId: Long,
        limit: Int
    ): Flow<List<Transaction>> {
        return transactionDao
            .observeRecentTransactions(
                userId = userId,
                limit = limit
            )
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override fun observeTotalIncome(
        userId: Long
    ): Flow<Long> {
        return transactionDao.observeTotalIncome(userId)
    }

    override fun observeTotalExpense(
        userId: Long
    ): Flow<Long> {
        return transactionDao.observeTotalExpense(userId)
    }

    override fun observeTotalSaving(
        userId: Long
    ): Flow<Long> {
        return transactionDao.observeTotalSaving(userId)
    }

    override fun observeTotalByTypeAndDateRange(
        userId: Long,
        type: TransactionType,
        startDate: String,
        endDate: String
    ): Flow<Long> {
        return transactionDao.observeTotalByTypeAndDateRange(
            userId = userId,
            type = type.name,
            startDate = startDate,
            endDate = endDate
        )
    }

    override suspend fun getTransactionById(
        userId: Long,
        transactionId: Long
    ): Transaction? {
        return transactionDao
            .getTransactionById(
                userId = userId,
                transactionId = transactionId
            )
            ?.toDomain()
    }

    override suspend fun insert(
        transaction: Transaction
    ): Long {
        return transactionDao.insert(
            transaction.toEntity()
        )
    }

    override suspend fun update(
        transaction: Transaction
    ) {
        transactionDao.update(
            transaction.toEntity()
        )
    }

    override suspend fun delete(
        transaction: Transaction
    ) {
        transactionDao.delete(
            transaction.toEntity()
        )
    }
}