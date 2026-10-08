package com.example.cashpeer.feature.transaction.domain.usecase

import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke(
        transaction: Transaction
    ): Result<Unit> {

        if (transaction.id <= 0) {
            return Result.failure(
                IllegalArgumentException(
                    "ID transaksi tidak valid"
                )
            )
        }

        repository.delete(transaction)

        return Result.success(Unit)
    }
}