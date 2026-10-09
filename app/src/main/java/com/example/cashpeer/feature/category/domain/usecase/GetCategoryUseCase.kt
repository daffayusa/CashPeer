package com.example.cashpeer.feature.category.domain.usecase

import com.example.cashpeer.feature.category.domain.model.Category
import com.example.cashpeer.feature.category.domain.repository.CategoryRepository
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryUseCase @Inject constructor(
    private val repository: CategoryRepository
){
    operator fun invoke(
        userId: Long,
        type: TransactionType
    ): Flow<List<Category>> {
        return repository.observeCategories(userId, type)
    }
}