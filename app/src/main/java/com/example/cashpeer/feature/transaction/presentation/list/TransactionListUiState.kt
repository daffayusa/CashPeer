package com.example.cashpeer.feature.transaction.presentation.list

import com.example.cashpeer.feature.transaction.domain.model.Transaction

data class TransactionListUiState(
    val isLoading: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val error: String? = null
)