package com.example.cashpeer.feature.category.domain.usecase

import com.example.cashpeer.feature.category.domain.model.Category
import com.example.cashpeer.feature.category.domain.repository.CategoryRepository
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(private val repository: CategoryRepository) {
    suspend operator fun invoke(category: Category): Result<Long> {
        if (category.name.isBlank()) return Result.failure(IllegalArgumentException("Nama kategori tidak boleh kosong"))
        return repository.addCategory(category)
    }
}
