package com.example.cashpeer.feature.savinggoal.presentation

import com.example.cashpeer.feature.savinggoal.domain.model.SavingFrequency

data class SavingGoalFormUiState(
    val goalId: Long? = null,
    val name: String = "",
    val targetAmount: String = "",
    val targetDate: String? = null,
    val note: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val savingFrequency: SavingFrequency = SavingFrequency.DAILY,
    val savingAmount: String = "",
    val error: String? = null
)