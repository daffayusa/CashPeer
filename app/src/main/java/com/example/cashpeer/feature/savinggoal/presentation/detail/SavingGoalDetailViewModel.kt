package com.example.cashpeer.feature.savinggoal.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashpeer.feature.savinggoal.domain.usecase.ObserveSavedAmountUseCase
import com.example.cashpeer.feature.savinggoal.domain.usecase.ObserveSavingGoalByIdUseCase
import com.example.cashpeer.feature.savinggoal.domain.usecase.ObserveTransactionsBySavingGoalUseCase
import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import com.example.cashpeer.feature.transaction.domain.usecase.AddTransactionUseCase
import com.example.cashpeer.feature.transaction.domain.usecase.DeleteTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class SavingGoalDetailViewModel @Inject constructor(
    private val observeSavingGoalByIdUseCase: ObserveSavingGoalByIdUseCase,
    private val observeSavedAmountUseCase: ObserveSavedAmountUseCase,
    private val observeTransactionsBySavingGoalUseCase: ObserveTransactionsBySavingGoalUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        const val TEMP_USER_ID = 1L
    }

    val goalId: Long = savedStateHandle.get<Long>("goalId") ?: 0L

    private val _uiState = MutableStateFlow(SavingGoalDetailUiState())
    val uiState: StateFlow<SavingGoalDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        if (goalId <= 0L) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Target tabungan tidak ditemukan."
            )
            return
        }

        viewModelScope.launch {
            combine(
                observeSavingGoalByIdUseCase(TEMP_USER_ID, goalId),
                observeSavedAmountUseCase(TEMP_USER_ID, goalId),
                observeTransactionsBySavingGoalUseCase(TEMP_USER_ID, goalId)
            ) { goal, savedAmount, transactions ->
                Triple(goal, savedAmount, transactions)
            }.catch { throwable ->
                if (throwable is CancellationException) throw throwable
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = throwable.message ?: "Gagal memuat detail target tabungan."
                )
            }.collect { (goal, savedAmount, transactions) ->
                _uiState.value = _uiState.value.copy(
                    goal = goal,
                    savedAmount = savedAmount,
                    transactions = transactions,
                    isLoading = false,
                    error = if (goal == null && !_uiState.value.isLoading) "Target tabungan tidak ditemukan." else null
                )
            }
        }
    }

    fun openDepositDialog() {
        val defaultAmount = _uiState.value.goal?.savingAmount?.takeIf { it > 0 }?.toString() ?: ""
        _uiState.value = _uiState.value.copy(
            isDepositDialogOpen = true,
            depositAmount = defaultAmount,
            depositNote = "",
            depositDate = LocalDate.now(),
            depositError = null
        )
    }

    fun closeDepositDialog() {
        _uiState.value = _uiState.value.copy(
            isDepositDialogOpen = false,
            depositAmount = "",
            depositNote = "",
            depositError = null
        )
    }

    fun setDepositAmount(amount: String) {
        if (amount.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(
                depositAmount = amount,
                depositError = null
            )
        }
    }

    fun setDepositNote(note: String) {
        _uiState.value = _uiState.value.copy(depositNote = note)
    }

    fun submitDeposit() {
        val amountLong = _uiState.value.depositAmount.toLongOrNull() ?: 0L
        if (amountLong <= 0L) {
            _uiState.value = _uiState.value.copy(depositError = "Nominal harus lebih dari 0")
            return
        }

        val goal = _uiState.value.goal ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmittingDeposit = true)
            val result = addTransactionUseCase(
                Transaction(
                    id = 0,
                    userId = TEMP_USER_ID,
                    categoryId = null,
                    savingGoalId = goal.id,
                    type = TransactionType.SAVING,
                    amount = amountLong,
                    transactionDate = _uiState.value.depositDate,
                    note = _uiState.value.depositNote.trim().ifEmpty { "Setoran ke ${goal.name}" }
                )
            )

            result.fold(
                onSuccess = {
                    closeDepositDialog()
                    _uiState.value = _uiState.value.copy(isSubmittingDeposit = false)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSubmittingDeposit = false,
                        depositError = error.message ?: "Gagal mencatat setoran tabungan"
                    )
                }
            )
        }
    }

    fun confirmDeleteTransaction(transaction: Transaction) {
        _uiState.value = _uiState.value.copy(transactionToDelete = transaction)
    }

    fun dismissDeleteTransaction() {
        _uiState.value = _uiState.value.copy(transactionToDelete = null)
    }

    fun deleteConfirmedTransaction() {
        val transaction = _uiState.value.transactionToDelete ?: return
        viewModelScope.launch {
            deleteTransactionUseCase(transaction)
            dismissDeleteTransaction()
        }
    }
}
