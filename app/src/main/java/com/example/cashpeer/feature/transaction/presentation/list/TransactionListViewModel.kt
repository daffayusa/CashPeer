package com.example.cashpeer.feature.transaction.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase
): ViewModel(){
    private companion object {
        const val TEMP_USER_ID = 1L
    }

    val uiState = getTransactionsUseCase(TEMP_USER_ID)
        .map { transactions ->
            TransactionListUiState(
                isLoading = false,
                transactions = transactions
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TransactionListUiState()
        )
}