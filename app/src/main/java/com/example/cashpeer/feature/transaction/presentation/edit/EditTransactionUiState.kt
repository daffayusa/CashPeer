package com.example.cashpeer.feature.transaction.presentation.edit

import com.example.cashpeer.feature.category.domain.model.Category
import com.example.cashpeer.feature.savinggoal.domain.model.SavingGoal
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import java.time.LocalDate

data class EditTransactionUiState(
    val transactionId: Long = 0L,
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val categoryId: Long? = null,
    val savingGoalId: Long? = null,
    val transactionDate: LocalDate = LocalDate.now(),
    val note: String = "",
    val categories: List<Category> = emptyList(),
    val savingGoals: List<SavingGoal> = emptyList(),
    val isLoadingCategories: Boolean = false,
    val isLoadingSavingGoals: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)
