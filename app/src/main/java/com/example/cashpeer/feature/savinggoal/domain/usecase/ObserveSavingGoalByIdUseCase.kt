package com.example.cashpeer.feature.savinggoal.domain.usecase

import com.example.cashpeer.feature.savinggoal.domain.model.SavingGoal
import com.example.cashpeer.feature.savinggoal.domain.repository.SavingGoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSavingGoalByIdUseCase @Inject constructor(
    private val repository: SavingGoalRepository
) {
    operator fun invoke(
        userId: Long,
        goalId: Long
    ): Flow<SavingGoal?> {
        return repository.observeSavingGoalById(
            userId = userId,
            goalId = goalId
        )
    }
}
