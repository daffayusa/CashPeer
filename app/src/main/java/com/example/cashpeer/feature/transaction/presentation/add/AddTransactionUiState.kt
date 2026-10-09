package com.example.cashpeer.feature.transaction.presentation.add

import com.example.cashpeer.feature.category.domain.model.Category
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import java.time.LocalDate

data class AddTransactionUiState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val categoryId: Long? = null,
    val savingGoalId: Long? = null,
    val transactionDate: LocalDate = LocalDate.now(),
    val note: String = "",
    val categories: List<Category> = emptyList(),
    val isLoadingCategories: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)