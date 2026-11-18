package com.example.cashpeer.feature.category.presentation

import com.example.cashpeer.feature.category.domain.model.Category
import com.example.cashpeer.feature.transaction.domain.model.TransactionType

data class CategoryManagementUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    // State for add/edit dialog
    val showDialog: Boolean = false,
    val editingCategory: Category? = null,   // null = mode tambah, non-null = mode edit
    val dialogName: String = "",
    val dialogType: TransactionType = TransactionType.EXPENSE,
    val dialogError: String? = null,
    val isSavingDialog: Boolean = false,
    // State for delete confirm
    val deletingCategory: Category? = null
)
