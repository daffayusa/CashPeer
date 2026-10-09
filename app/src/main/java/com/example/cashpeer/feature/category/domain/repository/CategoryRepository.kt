package com.example.cashpeer.feature.category.domain.repository

import com.example.cashpeer.feature.category.domain.model.Category
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface CategoryRepository{
    fun observeCategories(
        userId: Long,
        type: TransactionType
    ): Flow<List<Category>>
}