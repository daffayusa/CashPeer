package com.example.cashpeer.feature.savinggoal.domain.usecase

import com.example.cashpeer.feature.savinggoal.domain.model.SavingGoal
import com.example.cashpeer.feature.savinggoal.domain.repository.SavingGoalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSavingGoalUseCase @Inject constructor(
    private val repository: SavingGoalRepository
){
    operator fun invoke(userId:Long): Flow<List<SavingGoal>>{
        return repository.observeSavingGoals(userId)
    }
}