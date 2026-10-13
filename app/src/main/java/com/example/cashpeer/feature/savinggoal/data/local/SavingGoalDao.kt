package com.example.cashpeer.feature.savinggoal.data.local

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import dagger.Provides
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingGoalDao {

    @Query(
        """
        SELECT *
        FROM saving_goals
        WHERE user_id = :userId
        ORDER BY created_at DESC, id DESC
        """
    )
    fun observeSavingGoals(
        userId: Long
    ): Flow<List<SavingGoalEntity>>

    @Query(
        """
        SELECT *
        FROM saving_goals
        WHERE id = :goalId
          AND user_id = :userId
        LIMIT 1
        """
    )
    suspend fun getSavingGoalById(
        userId: Long,
        goalId: Long
    ): SavingGoalEntity?

    @Insert
    suspend fun insert(
        savingGoal: SavingGoalEntity
    ): Long

    @Update
    suspend fun update(
        savingGoal: SavingGoalEntity
    )

    @Delete
    suspend fun delete(
        savingGoal: SavingGoalEntity
    )

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0)
        FROM transactions
        WHERE user_id = :userId
          AND saving_goal_id = :goalId
          AND type = 'SAVING'
        """
    )
    fun observeSavedAmount(
        userId: Long,
        goalId: Long
    ): Flow<Long>
}