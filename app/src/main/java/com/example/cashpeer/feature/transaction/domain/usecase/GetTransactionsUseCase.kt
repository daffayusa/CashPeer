package com.example.cashpeer.feature.transaction.domain.usecase

import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository
){
    operator fun invoke(
        userId: Long
    ): Flow<List<Transaction>>{
        return repository.observeTransactions(userId)
    }
}