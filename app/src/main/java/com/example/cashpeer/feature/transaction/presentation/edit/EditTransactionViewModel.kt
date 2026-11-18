package com.example.cashpeer.feature.transaction.presentation.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashpeer.feature.category.data.local.DefaultCategorySeeder
import com.example.cashpeer.feature.category.domain.usecase.GetCategoryUseCase
import com.example.cashpeer.feature.savinggoal.domain.usecase.ObserveSavingGoalUseCase
import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import com.example.cashpeer.feature.transaction.domain.usecase.GetTransactionByIdUseCase
import com.example.cashpeer.feature.transaction.domain.usecase.UpdateTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class EditTransactionViewModel @Inject constructor(
    private val getTransactionByIdUseCase: GetTransactionByIdUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val getCategoryUseCase: GetCategoryUseCase,
    private val defaultCategorySeeder: DefaultCategorySeeder,
    private val observeSavingGoalUseCase: ObserveSavingGoalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditTransactionUiState())
    val uiState: StateFlow<EditTransactionUiState> = _uiState.asStateFlow()

    private var categoryJob: Job? = null

    fun loadTransaction(transactionId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            defaultCategorySeeder.seedIfNeeded(TEMP_USER_ID)
            val transaction = getTransactionByIdUseCase(TEMP_USER_ID, transactionId)
            if (transaction == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Transaksi tidak ditemukan"
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(
                transactionId = transaction.id,
                type = transaction.type,
                amount = transaction.amount.toString(),
                categoryId = transaction.categoryId,
                savingGoalId = transaction.savingGoalId,
                transactionDate = transaction.transactionDate,
                note = transaction.note ?: "",
                isLoading = false
            )
            observeCategories(transaction.type)
            observeSavingGoals()
        }
    }

    private fun observeSavingGoals() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingSavingGoals = true)
            try {
                observeSavingGoalUseCase(TEMP_USER_ID).collect { goals ->
                    _uiState.value = _uiState.value.copy(
                        savingGoals = goals,
                        isLoadingSavingGoals = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoadingSavingGoals = false)
            }
        }
    }

    fun setType(type: TransactionType) {
        _uiState.value = _uiState.value.copy(
            type = type,
            categoryId = null,
            savingGoalId = null,
            categories = emptyList(),
            isLoadingCategories = type != TransactionType.SAVING,
            error = null
        )
        observeCategories(type)
    }

    private fun observeCategories(type: TransactionType) {
        categoryJob?.cancel()
        if (type == TransactionType.SAVING) {
            _uiState.value = _uiState.value.copy(categories = emptyList(), isLoadingCategories = false)
            return
        }
        categoryJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingCategories = true)
            try {
                getCategoryUseCase(TEMP_USER_ID, type).collect { categories ->
                    _uiState.value = _uiState.value.copy(
                        categories = categories,
                        categoryId = _uiState.value.categoryId?.takeIf { id -> categories.any { it.id == id } },
                        isLoadingCategories = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoadingCategories = false, error = e.message)
            }
        }
    }

    fun setAmount(amount: String) {
        if (amount.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(amount = amount, error = null)
        }
    }

    fun setCategoryId(id: Long?) {
        _uiState.value = _uiState.value.copy(categoryId = id, error = null)
    }

    fun setSavingGoalId(id: Long?) {
        _uiState.value = _uiState.value.copy(savingGoalId = id, error = null)
    }

    fun setDate(date: java.time.LocalDate) {
        _uiState.value = _uiState.value.copy(transactionDate = date, error = null)
    }

    fun setNote(note: String) {
        _uiState.value = _uiState.value.copy(note = note, error = null)
    }

    fun save() {
        if (_uiState.value.isSaving) return
        val state = _uiState.value

        if (state.amount.toLongOrNull() == null || state.amount.toLong() <= 0L) {
            _uiState.value = state.copy(error = "Masukkan nominal transaksi yang valid")
            return
        }
        if (state.type != TransactionType.SAVING && state.categoryId == null) {
            _uiState.value = state.copy(error = "Pilih kategori transaksi terlebih dahulu")
            return
        }
        if (state.type == TransactionType.SAVING && state.savingGoalId == null) {
            _uiState.value = state.copy(error = "Pilih tujuan tabungan terlebih dahulu")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)
            try {
                val result = updateTransactionUseCase(
                    Transaction(
                        id = state.transactionId,
                        userId = TEMP_USER_ID,
                        categoryId = if (state.type != TransactionType.SAVING) state.categoryId else null,
                        savingGoalId = if (state.type == TransactionType.SAVING) state.savingGoalId else null,
                        type = state.type,
                        amount = state.amount.toLong(),
                        transactionDate = state.transactionDate,
                        note = state.note.ifBlank { null }
                    )
                )
                result.fold(
                    onSuccess = { _uiState.value = _uiState.value.copy(isSaving = false, isSaved = true) },
                    onFailure = { e -> _uiState.value = _uiState.value.copy(isSaving = false, error = e.message ?: "Gagal menyimpan") }
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, error = e.message ?: "Gagal menyimpan")
            }
        }
    }

    private companion object {
        const val TEMP_USER_ID = 1L
    }
}
