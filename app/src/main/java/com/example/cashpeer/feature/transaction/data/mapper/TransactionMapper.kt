package com.example.cashpeer.feature.transaction.data.mapper

import com.example.cashpeer.feature.transaction.data.local.TransactionEntity
import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import java.time.LocalDate

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        userId = userId,
        categoryId = categoryId,
        savingGoalId = savingGoalId,
        type = TransactionType.valueOf(type),
        amount = amount,
        transactionDate = LocalDate.parse(transactionDate),
        note = note
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        userId = userId,
        categoryId = categoryId,
        savingGoalId = savingGoalId,
        type = type.name,
        amount = amount,
        transactionDate = transactionDate.toString(),
        note = note
//        createdAt = createdAt,
//        updatedAt = updatedAt
    )
}