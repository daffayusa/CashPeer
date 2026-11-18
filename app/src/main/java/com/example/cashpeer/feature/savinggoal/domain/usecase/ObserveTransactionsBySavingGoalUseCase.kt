package com.example.cashpeer.feature.savinggoal.domain.usecase

import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTransactionsBySavingGoalUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(
        userId: Long,
        goalId: Long
    ): Flow<List<Transaction>> {
        return transactionRepository.observeTransactionsBySavingGoal(
            userId = userId,
            goalId = goalId
        )
    }
}
