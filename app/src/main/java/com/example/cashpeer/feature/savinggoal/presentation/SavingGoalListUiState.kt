package com.example.cashpeer.feature.savinggoal.presentation

import com.example.cashpeer.feature.savinggoal.presentation.model.SavingGoalItemUi

data class SavingGoalListUiState (
    val goals: List<SavingGoalItemUi> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)