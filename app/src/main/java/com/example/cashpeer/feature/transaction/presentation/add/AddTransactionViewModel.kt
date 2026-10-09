package com.example.cashpeer.feature.transaction.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashpeer.feature.category.data.local.DefaultCategorySeeder
import com.example.cashpeer.feature.category.domain.usecase.GetCategoryUseCase
import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import com.example.cashpeer.feature.transaction.domain.usecase.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getCategoryUseCase: GetCategoryUseCase,
    private val defaultCategorySeeder: DefaultCategorySeeder
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    private var categoryJob: Job? = null

    init {
        viewModelScope.launch {
            defaultCategorySeeder.seedIfNeeded(TEMP_USER_ID)
            observeCategories(_uiState.value.type)
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
            _uiState.value = _uiState.value.copy(
                categories = emptyList(),
                isLoadingCategories = false
            )
            return
        }

        categoryJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingCategories = true
            )

            try {
                getCategoryUseCase(TEMP_USER_ID, type).collect { categories ->
                    val currentCategoryId = _uiState.value.categoryId

                    _uiState.value = _uiState.value.copy(
                        categories = categories,
                        categoryId = currentCategoryId?.takeIf { id ->
                            categories.any { it.id == id }
                        },
                        isLoadingCategories = false
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingCategories = false,
                    error = e.message ?: "Gagal memuat kategori"
                )
            }
        }
    }

    fun setAmount(amount: String) {
        if (amount.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(
                amount = amount,
                error = null
            )
        }
    }

    fun setCategoryId(categoryId: Long?) {
        _uiState.value = _uiState.value.copy(
            categoryId = categoryId,
            error = null
        )
    }

    fun setSavingGoalId(savingGoalId: Long?) {
        _uiState.value = _uiState.value.copy(
            savingGoalId = savingGoalId,
            error = null
        )
    }

    fun setDate(date: java.time.LocalDate) {
        _uiState.value = _uiState.value.copy(
            transactionDate = date,
            error = null
        )
    }

    fun setNote(note: String) {
        _uiState.value = _uiState.value.copy(
            note = note,
            error = null
        )
    }

    fun save(onSuccess: () -> Unit) {
        if (_uiState.value.isSaving) return
        val state = _uiState.value

        if (state.amount.toLongOrNull() == null ||
            state.amount.toLong() <= 0L
        ) {
            _uiState.value = state.copy(
                error = "Masukkan nominal transaksi yang valid"
            )
            return
        }

        if (state.type != TransactionType.SAVING &&
            state.categoryId == null
        ) {
            _uiState.value = state.copy(
                error = "Pilih kategori transaksi terlebih dahulu"
            )
            return
        }

        if (state.type == TransactionType.SAVING &&
            state.savingGoalId == null
        ) {
            _uiState.value = state.copy(
                error = "Pilih tujuan tabungan terlebih dahulu"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                error = null
            )

            try {
                val transaction = Transaction(
                    id = 0L,
                    userId = TEMP_USER_ID,
                    categoryId = state.categoryId,
                    savingGoalId = state.savingGoalId,
                    type = state.type,
                    amount = state.amount.toLong(),
                    transactionDate = state.transactionDate,
                    note = state.note.ifBlank { null }
                )

                addTransactionUseCase(transaction)

                _uiState.value = _uiState.value.copy(
                    isSaving = false
                )

                onSuccess()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Gagal menyimpan transaksi"
                )
            }
        }
    }

    private companion object {
        const val TEMP_USER_ID = 1L
    }
}