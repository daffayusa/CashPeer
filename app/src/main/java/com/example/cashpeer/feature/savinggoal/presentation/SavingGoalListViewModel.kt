package com.example.cashpeer.feature.savinggoal.presentation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashpeer.feature.savinggoal.domain.usecase.ObserveSavedAmountUseCase
import com.example.cashpeer.feature.savinggoal.domain.usecase.ObserveSavingGoalUseCase
import com.example.cashpeer.feature.savinggoal.presentation.model.SavingGoalItemUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalMaterial3Api::class)
@HiltViewModel
class SavingGoalListViewModel @Inject constructor(
    observeSavingGoalsUseCase : ObserveSavingGoalUseCase,
    private val observeSavedAmountUseCase: ObserveSavedAmountUseCase
): ViewModel(){
   companion object{
       private const val TEMP_USER_ID = 1L
   }

   val uiState = observeSavingGoalsUseCase(TEMP_USER_ID)
       .flatMapLatest { goals ->
           if (goals.isEmpty()) {
               flow {
                   emit(SavingGoalListUiState(isLoading = false))
               }
           } else {
               val progressFlows = goals.map { goal ->
                   observeSavedAmountUseCase(TEMP_USER_ID, goal.id)
                       .map { savedAmount ->
                           SavingGoalItemUi(
                               goal = goal,
                               savedAmount = savedAmount
                           )
                       }
               }

               combine(progressFlows) { items ->
                   SavingGoalListUiState(
                       goals = items.toList(),
                       isLoading = false
                   )
               }
           }
       }
       .catch { throwable ->
           if(throwable is CancellationException) throw throwable
           emit(
               SavingGoalListUiState(
                   isLoading = false,
                   error = throwable.message ?: "Gagal memuat target tabungan."
               )
           )
       }
       .stateIn(
           scope = viewModelScope,
           started = SharingStarted.WhileSubscribed(5_000),
           initialValue = SavingGoalListUiState()
       )
}