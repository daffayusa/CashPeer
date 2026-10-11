package com.example.cashpeer.feature.category.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("""
        SELECT *
        FROM categories
        WHERE user_id = :userId
        AND type = :type
        ORDER BY name ASC
    """)
    fun observeCategories(
        userId: Long,
        type: String
    ): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategories(
        categories: List<CategoryEntity>
    ): List<Long>

    @Query("""
    SELECT COUNT(*)
    FROM categories
    WHERE user_id = :userId
    """)
    suspend fun countCategories(userId: Long): Int
}