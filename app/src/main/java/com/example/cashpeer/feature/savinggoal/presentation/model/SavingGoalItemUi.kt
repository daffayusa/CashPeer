package com.example.cashpeer.feature.savinggoal.presentation.model

import com.example.cashpeer.feature.savinggoal.domain.model.SavingGoal
import java.time.LocalDate

data class SavingGoalItemUi (
    val goal: SavingGoal,
    val savedAmount: Long = 0L
){
    val remainingAmount: Long
        get() = (goal.targetAmount - savedAmount).coerceAtLeast(0L)

    val progress: Float
        get() = if(goal.targetAmount > 0L){
            (savedAmount.toDouble() / goal.targetAmount)
                .coerceIn(0.0,1.0)
                .toFloat()
        }else{
            0f
        }

    val isCompleted: Boolean
        get() = savedAmount>= goal.targetAmount

    val targetDate: LocalDate?
        get() = goal.targetDate
}