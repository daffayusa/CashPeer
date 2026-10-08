package com.example.cashpeer.feature.transaction.domain.usecase

import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import com.example.cashpeer.feature.transaction.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke(
        transaction: Transaction
    ): Result<Long> {

        if (transaction.amount <= 0) {
            return Result.failure(
                IllegalArgumentException("Jumlah transaksi harus lebih dari 0")
            )
        }

        when (transaction.type) {

            TransactionType.INCOME,
            TransactionType.EXPENSE -> {
                if (transaction.categoryId == null) {
                    return Result.failure(
                        IllegalArgumentException(
                            "Kategori harus dipilih"
                        )
                    )
                }

                if (transaction.savingGoalId != null) {
                    return Result.failure(
                        IllegalArgumentException(
                            "Transaksi pemasukan/pengeluaran tidak boleh memiliki saving goal"
                        )
                    )
                }
            }

            TransactionType.SAVING -> {
                if (transaction.savingGoalId == null) {
                    return Result.failure(
                        IllegalArgumentException(
                            "Saving goal harus dipilih"
                        )
                    )

                    if (transaction.categoryId != null) {
                        return Result.failure(
                            IllegalArgumentException(
                                "Transaksi saving tidak boleh memiliki kategori"
                            )
                        )
                    }
                }
            }
        }
        return Result.success(
            repository.insert(transaction)
        )
    }

}