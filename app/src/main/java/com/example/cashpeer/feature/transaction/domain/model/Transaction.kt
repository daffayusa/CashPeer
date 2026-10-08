package com.example.cashpeer.feature.transaction.domain.model

import java.time.LocalDate

data class Transaction(
    val id: Long,
    val userId: Long,
    val categoryId: Long?,
    val savingGoalId: Long?,
    val type: TransactionType,
    val amount: Long,
    val transactionDate: LocalDate,
    val note: String?
)