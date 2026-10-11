package com.example.cashpeer.feature.transaction.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashpeer.feature.category.domain.usecase.GetCategoryUseCase
import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import com.example.cashpeer.feature.transaction.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getCategoryUseCase: GetCategoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionListUiState())
    val uiState: StateFlow<TransactionListUiState> =
        _uiState.asStateFlow()

    init {
        observeTransactions()
    }

    private fun observeTransactions() {
        viewModelScope.launch {
            try {
                combine(
                    getTransactionsUseCase(TEMP_USER_ID),
                    getCategoryUseCase(TEMP_USER_ID, TransactionType.INCOME),
                    getCategoryUseCase(TEMP_USER_ID, TransactionType.EXPENSE)
                ) { transactions, incomeCategories, expenseCategories ->
                    TransactionListUiState(
                        transactions = transactions,
                        categories = incomeCategories + expenseCategories,
                        isLoading = false
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Gagal memuat transaksi"
                )
            }
        }
    }
    fun setTransactionType(type: TransactionType?){
        _uiState.value = _uiState.value.copy(selectedType = type)
    }

    private companion object {
        const val TEMP_USER_ID = 1L
    }
}