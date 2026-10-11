package com.example.cashpeer.feature.category.domain.model

import com.example.cashpeer.feature.transaction.domain.model.TransactionType

data class Category (
    val id: Long,
    val userId: Long,
    val name: String,
    val type: TransactionType
)