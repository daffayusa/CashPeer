package com.example.cashpeer.feature.savinggoal.domain.usecase

import com.example.cashpeer.feature.savinggoal.domain.model.SavingGoal
import com.example.cashpeer.feature.savinggoal.domain.repository.SavingGoalRepository
import javax.inject.Inject

class DeleteSavingGoalUseCase @Inject constructor(
    private val repository: SavingGoalRepository
) {
    suspend operator fun invoke(goal: SavingGoal): Result<Unit> {
        return runCatching {
            require(goal.id > 0L) {
                "ID target tabungan tidak valid."
            }

            repository.delete(goal)
        }
    }
}