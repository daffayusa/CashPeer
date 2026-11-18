package com.example.cashpeer.feature.category.data.repository

import com.example.cashpeer.feature.category.data.local.CategoryDao
import com.example.cashpeer.feature.category.data.local.CategoryEntity
import com.example.cashpeer.feature.category.data.mapper.toDomain
import com.example.cashpeer.feature.category.domain.model.Category
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import com.example.cashpeer.feature.category.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
): CategoryRepository {
    override fun observeCategories(userId: Long, type: TransactionType): Flow<List<Category>> {
        return categoryDao.observeCategories(
            userId = userId,
            type = type.name
        ) .map { entities ->
            entities.map{
                it.toDomain()
            }
        }
    }

    override fun observeAllCategories(userId: Long): Flow<List<Category>> {
        return categoryDao.observeAllCategories(userId).map { it.map { e -> e.toDomain() } }
    }

    override suspend fun addCategory(category: Category): Result<Long> {
        return runCatching {
            categoryDao.insertCategory(
                CategoryEntity(userId = category.userId, name = category.name.trim(), type = category.type.name)
            )
        }
    }

    override suspend fun updateCategory(category: Category): Result<Unit> {
        return runCatching {
            categoryDao.updateCategory(
                CategoryEntity(id = category.id, userId = category.userId, name = category.name.trim(), type = category.type.name)
            )
        }
    }

    override suspend fun deleteCategory(category: Category): Result<Unit> {
        return runCatching {
            categoryDao.deleteCategory(
                CategoryEntity(id = category.id, userId = category.userId, name = category.name.trim(), type = category.type.name)
            )
        }
    }
}