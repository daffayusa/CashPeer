package com.example.cashpeer.feature.profile.presentation

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashpeer.core.ui.theme.AppThemeMode
import com.example.cashpeer.core.ui.theme.ThemeManager
import com.example.cashpeer.feature.category.domain.usecase.ObserveAllCategoriesUseCase
import com.example.cashpeer.feature.savinggoal.domain.usecase.ObserveSavingGoalUseCase
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import com.example.cashpeer.feature.transaction.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val observeAllCategoriesUseCase: ObserveAllCategoriesUseCase,
    private val observeSavingGoalUseCase: ObserveSavingGoalUseCase
) : ViewModel() {

    private val prefs = context.getSharedPreferences("cashpeer_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val savedName = prefs.getString("user_name", "") ?: ""
        val savedThemeStr = prefs.getString("app_theme_mode", AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
        val themeMode = runCatching { AppThemeMode.valueOf(savedThemeStr) }.getOrDefault(AppThemeMode.SYSTEM)

        _uiState.value = _uiState.value.copy(
            userName = savedName,
            themeMode = themeMode
        )
    }

    fun startEditName() {
        _uiState.value = _uiState.value.copy(
            isEditingName = true,
            editNameInput = _uiState.value.userName
        )
    }

    fun setEditNameInput(name: String) {
        _uiState.value = _uiState.value.copy(editNameInput = name)
    }

    fun cancelEditName() {
        _uiState.value = _uiState.value.copy(
            isEditingName = false,
            editNameInput = ""
        )
    }

    fun saveName() {
        val name = _uiState.value.editNameInput.trim()
        prefs.edit().putString("user_name", name).apply()
        _uiState.value = _uiState.value.copy(
            userName = name,
            isEditingName = false,
            editNameInput = "",
            saveSuccess = true
        )
        viewModelScope.launch {
            delay(2000)
            _uiState.value = _uiState.value.copy(saveSuccess = false)
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        ThemeManager.setThemeMode(context, mode)
        _uiState.value = _uiState.value.copy(themeMode = mode)
    }

    fun exportCsv() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true)
            try {
                val transactions = getTransactionsUseCase(TEMP_USER_ID).first()
                val categories = observeAllCategoriesUseCase(TEMP_USER_ID).first()
                val savingGoals = observeSavingGoalUseCase(TEMP_USER_ID).first()

                val categoryMap = categories.associate { it.id to it.name }
                val savingGoalMap = savingGoals.associate { it.id to it.name }

                val header = "ID,Tanggal,Tipe,Kategori,Nominal,Catatan\n"
                val rows = transactions.joinToString("\n") { t ->
                    val typeLabel = when (t.type) {
                        TransactionType.INCOME -> "Pemasukan"
                        TransactionType.EXPENSE -> "Pengeluaran"
                        TransactionType.SAVING -> "Tabungan"
                    }
                    val catName = when (t.type) {
                        TransactionType.SAVING -> t.savingGoalId?.let { savingGoalMap[it] } ?: "Tabungan"
                        else -> t.categoryId?.let { categoryMap[it] } ?: "Transaksi"
                    }
                    val escapedNote = t.note.orEmpty().replace("\"", "\"\"")

                    "${t.id},${t.transactionDate},\"$typeLabel\",\"$catName\",${t.amount},\"$escapedNote\""
                }

                val csvContent = header + rows

                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "CashPeer_Transactions_${LocalDate.now()}.csv")
                    putExtra(Intent.EXTRA_TEXT, csvContent)
                }
                val shareIntent = Intent.createChooser(sendIntent, "Ekspor Data Transaksi").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(shareIntent)

                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    exportMessage = "Data transaksi siap dibagikan/disimpan!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    exportMessage = "Gagal mengekspor data: ${e.message}"
                )
            }

            delay(3000)
            _uiState.value = _uiState.value.copy(exportMessage = null)
        }
    }

    private companion object {
        const val TEMP_USER_ID = 1L
    }
}
