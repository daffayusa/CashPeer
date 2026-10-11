package com.example.cashpeer.feature.category.data.repository

import com.example.cashpeer.feature.category.data.local.CategoryDao
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
}