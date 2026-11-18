package com.example.cashpeer.feature.savinggoal.presentation.detail

import com.example.cashpeer.feature.savinggoal.domain.model.SavingGoal
import com.example.cashpeer.feature.savinggoal.domain.model.estimateCompletionDate
import com.example.cashpeer.feature.transaction.domain.model.Transaction
import java.time.LocalDate

data class SavingGoalDetailUiState(
    val goal: SavingGoal? = null,
    val savedAmount: Long = 0L,
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,

    // Quick deposit dialog
    val isDepositDialogOpen: Boolean = false,
    val depositAmount: String = "",
    val depositNote: String = "",
    val depositDate: LocalDate = LocalDate.now(),
    val isSubmittingDeposit: Boolean = false,
    val depositError: String? = null,

    // Delete transaction confirmation dialog
    val transactionToDelete: Transaction? = null
) {
    val targetAmount: Long
        get() = goal?.targetAmount ?: 0L

    val remainingAmount: Long
        get() = (targetAmount - savedAmount).coerceAtLeast(0L)

    val progress: Float
        get() = if (targetAmount > 0L) {
            (savedAmount.toDouble() / targetAmount).coerceIn(0.0, 1.0).toFloat()
        } else {
            0f
        }

    val progressPercent: Int
        get() = (progress * 100).toInt()

    val isCompleted: Boolean
        get() = remainingAmount == 0L && targetAmount > 0L

    val estimatedCompletionDate: LocalDate?
        get() = goal?.let {
            estimateCompletionDate(
                targetAmount = it.targetAmount,
                savedAmount = savedAmount,
                savingAmount = it.savingAmount,
                frequency = it.savingFrequency
            )
        }
}
