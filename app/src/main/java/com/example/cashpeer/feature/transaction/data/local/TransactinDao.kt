package com.example.cashpeer.feature.transaction.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query(
        """
        SELECT *
        FROM transactions
        WHERE user_id = :userId
        ORDER BY transaction_date DESC, id DESC
        """
    )
    fun observeTransactions(
        userId:Long
    ): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT *
        FROM transactions
        WHERE id = :transactionId
          AND user_id = :userId
        LIMIT 1
        """
    )
    suspend fun getTransactionById(
        userId: Long,
        transactionId: Long
    ): TransactionEntity?

    @Query(
        """
    SELECT *
    FROM transactions
    WHERE user_id = :userId
      AND type = :type
    ORDER BY transaction_date DESC, id DESC
    """
    )
    fun observeTransactionByType(
        userId: Long,
        type: String
    ): Flow<List<TransactionEntity>>

    @Query(
        """
    SELECT *
    FROM transactions
    WHERE user_id = :userId
    ORDER BY transaction_date DESC, id DESC
    LIMIT :limit
    """
    )
    fun observeRecentTransactions(
        userId: Long,
        limit: Int = 5
    ): Flow<List<TransactionEntity>>

    @Query(
        """
    SELECT COALESCE(SUM(amount), 0)
    FROM transactions
    WHERE user_id = :userId
      AND type = 'INCOME'
    """
    )
    fun observeTotalIncome(
        userId: Long
    ): Flow<Long>

    @Query(
        """
    SELECT COALESCE(SUM(amount), 0)
    FROM transactions
    WHERE user_id = :userId
      AND type = 'EXPENSE'
    """
    )
    fun observeTotalExpense(
        userId: Long
    ): Flow<Long>

    @Query(
        """
    SELECT COALESCE(SUM(amount), 0)
    FROM transactions
    WHERE user_id = :userId
      AND type = 'SAVING'
    """
    )
    fun observeTotalSaving(
        userId: Long
    ): Flow<Long>

    @Query(
        """
    SELECT COALESCE(SUM(amount), 0)
    FROM transactions
    WHERE user_id = :userId
      AND type = :type
      AND transaction_date >= :startDate
      AND transaction_date < :endDate
    """
    )
    fun observeTotalByTypeAndDateRange(
        userId: Long,
        type: String,
        startDate: String,
        endDate: String
    ): Flow<Long>


    @Insert
    suspend fun insert(
        transaction: TransactionEntity
    ): Long

    @Update
    suspend fun update(
        transaction: TransactionEntity
    )

    @Delete
    suspend fun delete(
        transaction: TransactionEntity
    )
}