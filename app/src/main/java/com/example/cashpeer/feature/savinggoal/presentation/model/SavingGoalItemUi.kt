package com.example.cashpeer.feature.savinggoal.presentation.model

import com.example.cashpeer.feature.savinggoal.domain.model.SavingGoal
import com.example.cashpeer.feature.savinggoal.domain.model.estimateCompletionDate
import java.time.LocalDate

data class SavingGoalItemUi(
    val goal: SavingGoal,
    val savedAmount: Long
) {
    val remainingAmount: Long
        get() = (goal.targetAmount - savedAmount).coerceAtLeast(0L)

    val progress: Float
        get() = if (goal.targetAmount > 0L) {
            (savedAmount.toDouble() / goal.targetAmount)
                .coerceIn(0.0, 1.0)
                .toFloat()
        } else {
            0f
        }

    val isCompleted: Boolean
        get() = remainingAmount == 0L

    val estimatedCompletionDate: LocalDate?
        get() = estimateCompletionDate(
            targetAmount = goal.targetAmount,
            savedAmount = savedAmount,
            savingAmount = goal.savingAmount,
            frequency = goal.savingFrequency
        )
}