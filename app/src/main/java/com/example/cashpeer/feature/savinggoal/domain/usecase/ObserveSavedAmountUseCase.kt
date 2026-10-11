package com.example.cashpeer.feature.savinggoal.domain.usecase

import com.example.cashpeer.feature.savinggoal.domain.repository.SavingGoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSavedAmountUseCase @Inject constructor(
    private val repository: SavingGoalRepository
){
    operator fun invoke(
        userId: Long,
        goalId: Long
    ): Flow<Long> {
        return repository.observeSavedAmount(
            userId = userId,
            goalId = goalId
        )
    }
}