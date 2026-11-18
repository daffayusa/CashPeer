package com.example.cashpeer.feature.category.domain.usecase

import com.example.cashpeer.feature.category.domain.model.Category
import com.example.cashpeer.feature.category.domain.repository.CategoryRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(private val repository: CategoryRepository) {
    suspend operator fun invoke(category: Category): Result<Unit> =
        repository.deleteCategory(category)
}
