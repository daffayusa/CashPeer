package com.example.cashpeer.feature.category.domain.repository

import com.example.cashpeer.feature.category.domain.model.Category
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface CategoryRepository{
    fun observeCategories(
        userId: Long,
        type: TransactionType
    ): Flow<List<Category>>

    fun observeAllCategories(userId: Long): Flow<List<Category>>
    suspend fun addCategory(category: Category): Result<Long>
    suspend fun updateCategory(category: Category): Result<Unit>
    suspend fun deleteCategory(category: Category): Result<Unit>
}