package com.example.cashpeer.feature.savinggoal.domain.repository

import com.example.cashpeer.feature.savinggoal.domain.model.SavingGoal
import kotlinx.coroutines.flow.Flow

interface SavingGoalRepository {

    fun observeSavingGoals(
        userId: Long
    ): Flow<List<SavingGoal>>

    suspend fun getSavingGoalById(
        userId: Long,
        goalId: Long
    ): SavingGoal?

    fun observeSavedAmount(
        userId: Long,
        goalId: Long
    ): Flow<Long>

    suspend fun insert(
        savingGoal: SavingGoal
    ): Long

    suspend fun update(
        savingGoal: SavingGoal
    )

    suspend fun delete(
        savingGoal: SavingGoal
    )
}