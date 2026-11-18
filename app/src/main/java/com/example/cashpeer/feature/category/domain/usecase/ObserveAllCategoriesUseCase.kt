package com.example.cashpeer.feature.category.domain.usecase

import com.example.cashpeer.feature.category.domain.model.Category
import com.example.cashpeer.feature.category.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAllCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    operator fun invoke(userId: Long): Flow<List<Category>> =
        repository.observeAllCategories(userId)
}
