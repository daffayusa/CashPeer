package com.example.cashpeer.feature.category.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashpeer.feature.category.domain.model.Category
import com.example.cashpeer.feature.category.domain.usecase.AddCategoryUseCase
import com.example.cashpeer.feature.category.domain.usecase.DeleteCategoryUseCase
import com.example.cashpeer.feature.category.domain.usecase.ObserveAllCategoriesUseCase
import com.example.cashpeer.feature.category.domain.usecase.UpdateCategoryUseCase
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class CategoryManagementViewModel @Inject constructor(
    private val observeAllCategoriesUseCase: ObserveAllCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryManagementUiState())
    val uiState: StateFlow<CategoryManagementUiState> = _uiState.asStateFlow()

    init {
        observeCategories()
    }

    private fun observeCategories() {
        viewModelScope.launch {
            try {
                observeAllCategoriesUseCase(TEMP_USER_ID).collect { categories ->
                    _uiState.value = _uiState.value.copy(categories = categories, isLoading = false)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    // Dialog untuk Tambah
    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(
            showDialog = true,
            editingCategory = null,
            dialogName = "",
            dialogType = TransactionType.EXPENSE,
            dialogError = null
        )
    }

    // Dialog untuk Edit
    fun showEditDialog(category: Category) {
        _uiState.value = _uiState.value.copy(
            showDialog = true,
            editingCategory = category,
            dialogName = category.name,
            dialogType = category.type,
            dialogError = null
        )
    }

    fun dismissDialog() {
        _uiState.value = _uiState.value.copy(showDialog = false, editingCategory = null, dialogError = null)
    }

    fun setDialogName(name: String) {
        _uiState.value = _uiState.value.copy(dialogName = name, dialogError = null)
    }

    fun setDialogType(type: TransactionType) {
        _uiState.value = _uiState.value.copy(dialogType = type, dialogError = null)
    }

    fun saveCategory() {
        val state = _uiState.value
        if (state.isSavingDialog) return
        _uiState.value = state.copy(isSavingDialog = true, dialogError = null)

        viewModelScope.launch {
            val result = if (state.editingCategory == null) {
                addCategoryUseCase(
                    Category(id = 0, userId = TEMP_USER_ID, name = state.dialogName.trim(), type = state.dialogType)
                )
            } else {
                updateCategoryUseCase(
                    state.editingCategory.copy(name = state.dialogName.trim(), type = state.dialogType)
                )
            }
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isSavingDialog = false, showDialog = false, editingCategory = null)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isSavingDialog = false, dialogError = e.message ?: "Gagal menyimpan")
                }
            )
        }
    }

    fun confirmDelete(category: Category) {
        _uiState.value = _uiState.value.copy(deletingCategory = category)
    }

    fun dismissDelete() {
        _uiState.value = _uiState.value.copy(deletingCategory = null)
    }

    fun deleteCategory() {
        val category = _uiState.value.deletingCategory ?: return
        viewModelScope.launch {
            deleteCategoryUseCase(category)
            _uiState.value = _uiState.value.copy(deletingCategory = null)
        }
    }

    private companion object {
        const val TEMP_USER_ID = 1L
    }
}
