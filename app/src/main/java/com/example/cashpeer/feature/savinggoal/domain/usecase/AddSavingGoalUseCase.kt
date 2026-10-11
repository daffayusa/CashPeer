package com.example.cashpeer.feature.savinggoal.domain.usecase

import com.example.cashpeer.feature.savinggoal.domain.model.SavingGoal
import com.example.cashpeer.feature.savinggoal.domain.repository.SavingGoalRepository
import javax.inject.Inject

class AddSavingGoalUseCase @Inject constructor(
    private val repository: SavingGoalRepository
){
    suspend operator fun invoke(
        goal: SavingGoal
    ): Result<Long>{
        return runCatching {
            require(goal.name.isNotBlank()) {
                "Nama target tabungan wajib diisi."
            }
            require(goal.targetAmount > 0L) {
                "Nominal target harus lebih dari 0."
            }

            repository.insert(goal)
        }
    }

}