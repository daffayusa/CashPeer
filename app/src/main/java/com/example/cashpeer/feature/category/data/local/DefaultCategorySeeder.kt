package com.example.cashpeer.feature.category.data.local

import javax.inject.Inject

class DefaultCategorySeeder @Inject constructor(
    private val categoryDao: CategoryDao
) {
    suspend fun seedIfNeeded(userId: Long) {
        if (categoryDao.countCategories(userId) > 0) return

        categoryDao.insertCategories(
            listOf(
                CategoryEntity(
                    userId = userId,
                    name = "Gaji",
                    type = "INCOME"
                ),
                CategoryEntity(
                    userId = userId,
                    name = "Bonus",
                    type = "INCOME"
                ),
                CategoryEntity(
                    userId = userId,
                    name = "Makanan",
                    type = "EXPENSE"
                ),
                CategoryEntity(
                    userId = userId,
                    name = "Transportasi",
                    type = "EXPENSE"
                ),
                CategoryEntity(
                    userId = userId,
                    name = "Belanja",
                    type = "EXPENSE"
                ),
                CategoryEntity(
                    userId = userId,
                    name = "Tagihan",
                    type = "EXPENSE"
                )
            )
        )
    }
}