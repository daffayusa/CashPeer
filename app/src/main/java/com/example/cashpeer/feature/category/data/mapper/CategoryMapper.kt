package com.example.cashpeer.feature.category.data.mapper

import com.example.cashpeer.feature.category.data.local.CategoryEntity
import com.example.cashpeer.feature.category.domain.model.Category
import com.example.cashpeer.feature.transaction.domain.model.TransactionType

fun CategoryEntity.toDomain(): Category{
    return Category(
        id = id,
        userId = userId,
        name = name,
        type = TransactionType.valueOf(type)
    )
}