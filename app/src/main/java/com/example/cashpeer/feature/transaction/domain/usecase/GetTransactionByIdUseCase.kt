package com.example.cashpeer.feature.transaction.domain.usecase

import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionByIdUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(
        userId: Long,
        transactionId: Long
    ): Transaction?{
        return repository.getTransactionById(
            userId,
            transactionId
        )
    }
}