package com.example.cashpeer.feature.transaction.presentation.add

import androidx.collection.arrayMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import com.example.cashpeer.feature.transaction.domain.usecase.AddTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase
) : ViewModel(){
    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    fun setType(type: TransactionType){
        _uiState.value = _uiState.value.copy(
            type =type,
            categoryId = null,
            savingGoalId = null,
            error = null
        )
    }

    fun setAmount(amount: String){
        if(amount.all {it.isDigit()}){
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
        val state = _uiState.value

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(
                    isSaving = true,
                    error = null
                )

                val transaction = Transaction(
                    id = 0L,
                    userId = TEMP_USER_ID,
                    categoryId = state.categoryId ?: 1L,
                    savingGoalId = state.savingGoalId,
                    type = state.type,
                    amount = state.amount.toLongOrNull() ?: 0L,
                    transactionDate = state.transactionDate,
                    note = state.note.ifBlank { null }
                )

                addTransactionUseCase(transaction)

                _uiState.value = _uiState.value.copy(
                    isSaving = false
                )

                onSuccess()

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