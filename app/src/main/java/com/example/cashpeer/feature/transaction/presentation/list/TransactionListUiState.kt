package com.example.cashpeer.feature.transaction.presentation.list

import com.example.cashpeer.feature.category.domain.model.Category
import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.model.TransactionType

data class TransactionListUiState(
    val transactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedType: TransactionType? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)