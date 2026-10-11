package com.example.cashpeer.feature.savinggoal.data.repository

import com.example.cashpeer.feature.savinggoal.data.Mapper.toDomain
import com.example.cashpeer.feature.savinggoal.data.Mapper.toEntity
import com.example.cashpeer.feature.savinggoal.data.local.SavingGoalDao
import com.example.cashpeer.feature.savinggoal.domain.model.SavingGoal
import com.example.cashpeer.feature.savinggoal.domain.repository.SavingGoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SavingGoalRepositoryImpl @Inject constructor(
    private val savingGoalDao: SavingGoalDao
) : SavingGoalRepository {

    override fun observeSavingGoals(
        userId: Long
    ): Flow<List<SavingGoal>> {
        return savingGoalDao.observeSavingGoals(userId).map { goals ->
            goals.map { it.toDomain() }
        }
    }

    override suspend fun getSavingGoalById(
        userId: Long,
        goalId: Long
    ): SavingGoal? {
        return savingGoalDao.getSavingGoalById(
            userId = userId,
            goalId = goalId
        )?.toDomain()
    }

    override fun observeSavedAmount(
        userId: Long,
        goalId: Long
    ): Flow<Long> {
        return savingGoalDao.observeSavedAmount(
            userId = userId,
            goalId = goalId
        )
    }

    override suspend fun insert(
        savingGoal: SavingGoal
    ): Long {
        return savingGoalDao.insert(savingGoal.toEntity())
    }

    override suspend fun update(
        savingGoal: SavingGoal
    ) {
        savingGoalDao.update(savingGoal.toEntity())
    }

    override suspend fun delete(
        savingGoal: SavingGoal
    ) {
        savingGoalDao.delete(savingGoal.toEntity())
    }
}