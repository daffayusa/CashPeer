package com.example.cashpeer.feature.savinggoal.domain.usecase

import com.example.cashpeer.feature.savinggoal.domain.model.SavingGoal
import com.example.cashpeer.feature.savinggoal.domain.repository.SavingGoalRepository
import javax.inject.Inject

class GetSavingGoalByIdUseCase @Inject constructor(
    private val repository: SavingGoalRepository
){
    suspend operator fun invoke(
        userId: Long,
        goalId: Long
    ): SavingGoal?{
        return repository.getSavingGoalById(
            userId = userId,
            goalId = goalId
        )

    }
}