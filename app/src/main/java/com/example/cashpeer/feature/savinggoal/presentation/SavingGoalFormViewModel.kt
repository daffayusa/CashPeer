package com.example.cashpeer.feature.savinggoal.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashpeer.feature.savinggoal.domain.model.SavingGoal
import com.example.cashpeer.feature.savinggoal.domain.usecase.AddSavingGoalUseCase
import com.example.cashpeer.feature.savinggoal.domain.usecase.GetSavingGoalByIdUseCase
import com.example.cashpeer.feature.savinggoal.domain.usecase.UpdateSavingGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SavingGoalFormViewModel @Inject constructor(
    private val addSavingGoalUseCase: AddSavingGoalUseCase,
    private val updateSavingGoalUseCase: UpdateSavingGoalUseCase,
    private val getSavingGoalByIdUseCase: GetSavingGoalByIdUseCase
) : ViewModel() {

    companion object {
        private const val TEMP_USER_ID = 1L
    }

    private val _uiState = MutableStateFlow(SavingGoalFormUiState())
    val uiState = _uiState.asStateFlow()

    fun loadGoal(goalId: Long) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null)
            }

            try {
                val goal = getSavingGoalByIdUseCase(
                    userId = TEMP_USER_ID,
                    goalId = goalId
                )

                if (goal == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Target tabungan tidak ditemukan."
                        )
                    }
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        goalId = goal.id,
                        name = goal.name,
                        targetAmount = goal.targetAmount.toString(),
                        targetDate = goal.targetDate?.toString(),
                        note = goal.note.orEmpty(),
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Gagal memuat target tabungan."
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, error = null) }
    }

    fun onTargetAmountChange(value: String) {
        if (value.isEmpty() || value.all(Char::isDigit)) {
            _uiState.update {
                it.copy(targetAmount = value, error = null)
            }
        }
    }

    fun onTargetDateChange(value: String?) {
        _uiState.update { it.copy(targetDate = value, error = null) }
    }

    fun onNoteChange(value: String) {
        _uiState.update { it.copy(note = value, error = null) }
    }

    fun save() {
        val current = _uiState.value

        if (current.isSaving || current.isLoading || current.isSaved) return

        val name = current.name.trim()
        val amount = current.targetAmount.toLongOrNull()
        val targetDate = try {
            current.targetDate?.let(LocalDate::parse)
        } catch (_: Exception) {
            null
        }

        when {
            name.isBlank() -> {
                _uiState.update {
                    it.copy(error = "Nama target tabungan wajib diisi.")
                }
                return
            }

            amount == null || amount <= 0L -> {
                _uiState.update {
                    it.copy(error = "Nominal target harus lebih dari 0.")
                }
                return
            }

            current.targetDate != null && targetDate == null -> {
                _uiState.update {
                    it.copy(error = "Tanggal target tidak valid.")
                }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            try {
                val goal = SavingGoal(
                    id = current.goalId ?: 0L,
                    userId = TEMP_USER_ID,
                    name = name,
                    targetAmount = amount,
                    targetDate = targetDate,
                    note = current.note.trim().ifBlank { null }
                )

                val result = if (current.goalId == null) {
                    addSavingGoalUseCase(goal)
                } else {
                    updateSavingGoalUseCase(goal).map { current.goalId }
                }

                result.fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(isSaving = false, isSaved = true)
                        }
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                error = error.message
                                    ?: "Gagal menyimpan target tabungan."
                            )
                        }
                    }
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Terjadi kesalahan saat menyimpan."
                    )
                }
            }
        }
    }
    fun onClearTargetDate() {
        _uiState.update {
            it.copy(targetDate = null, error = null)
        }
    }
}