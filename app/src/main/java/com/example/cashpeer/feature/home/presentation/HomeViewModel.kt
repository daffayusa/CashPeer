package com.example.cashpeer.feature.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashpeer.feature.category.domain.repository.CategoryRepository
import com.example.cashpeer.feature.savinggoal.domain.model.estimateCompletionDate
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import com.example.cashpeer.feature.transaction.domain.repository.TransactionRepository
import com.example.cashpeer.feature.savinggoal.domain.repository.SavingGoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val savingGoalRepository: SavingGoalRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val userId = 1L

    private val selectedMonth = MutableStateFlow(YearMonth.now())

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HomeUiState()
    )

    init {
        observeHome()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeHome() {
        val goalsWithProgress = savingGoalRepository
            .observeSavingGoals(userId)
            .flatMapLatest { goals ->
                if (goals.isEmpty()) {
                    flowOf(emptyList<HomeSavingGoalUi>())
                } else {
                    combine(
                        goals.map { goal ->
                            savingGoalRepository
                                .observeSavedAmount(userId, goal.id)
                                .map { savedAmount ->
                                    val estimatedDate = if (savedAmount >= goal.targetAmount) {
                                        null
                                    } else {
                                        estimateCompletionDate(
                                            targetAmount = goal.targetAmount,
                                            savedAmount = savedAmount,
                                            savingAmount = goal.savingAmount,
                                            frequency = goal.savingFrequency
                                        )
                                    }

                                    HomeSavingGoalUi(
                                        id = goal.id,
                                        name = goal.name,
                                        savedAmount = savedAmount,
                                        targetAmount = goal.targetAmount,
                                        estimatedCompletion = estimatedDate?.format(
                                            DateTimeFormatter.ofPattern(
                                                "dd MMM yyyy",
                                                Locale("id", "ID")
                                            )
                                        )
                                    )
                                }
                        }
                    ) { items -> items.toList() }
                }
            }

        val monthlySummary = selectedMonth.flatMapLatest { month ->
            combine(
                transactionRepository.observeTotalByTypeAndDateRange(
                    userId,
                    TransactionType.INCOME,
                    month.atDay(1).toString(),
                    month.atEndOfMonth().toString()
                ),
                transactionRepository.observeTotalByTypeAndDateRange(
                    userId,
                    TransactionType.EXPENSE,
                    month.atDay(1).toString(),
                    month.atEndOfMonth().toString()
                )
            ) { income, expense ->
                Triple(month, income, expense)
            }
        }
        val incomeCategories = categoryRepository.observeCategories(userId, TransactionType.INCOME)
        val expenseCategories = categoryRepository.observeCategories(userId, TransactionType.EXPENSE)
        val categoryNames = combine(
            incomeCategories,
            expenseCategories
        ){income, expense ->
            (income+expense).associate { category ->
                category.id to category.name
            }
        }

        viewModelScope.launch {
            combine(
                transactionRepository.observeTotalIncome(userId),
                transactionRepository.observeTotalExpense(userId),
                transactionRepository.observeTotalSaving(userId),
                monthlySummary,
                transactionRepository.observeRecentTransactions(userId, 4),
                goalsWithProgress,
                categoryNames
            ) { values ->
                @Suppress("UNCHECKED_CAST")
                val income = values[0] as Long
                val expense = values[1] as Long
                val saving = values[2] as Long
                val monthly = values[3] as Triple<YearMonth, Long, Long>
                val transactions = values[4] as List<com.example.cashpeer.feature.transaction.domain.model.Transaction>
                val goals = values[5] as List<HomeSavingGoalUi>
                val categories = values[6] as Map<Long, String>

                HomeUiState(
                    totalBalance = income - expense - saving,
                    monthlyIncome = monthly.second,
                    monthlyExpense = monthly.third,
                    monthLabel = monthly.first.format(
                        DateTimeFormatter.ofPattern(
                            "MMMM yyyy",
                            Locale("id", "ID")
                        )
                    ),
                    recentTransactions = transactions.map { transaction ->
                        val savingGoalName = goals
                            .firstOrNull { it.id == transaction.savingGoalId }
                            ?.name

                        val categoryName = when (transaction.type) {
                            TransactionType.SAVING ->
                                savingGoalName ?: "Tabungan"

                            TransactionType.INCOME,
                            TransactionType.EXPENSE ->
                                transaction.categoryId?.let { categories[it] }
                                    ?: "Transaksi"
                        }

                        HomeTransactionUi(
                            id = transaction.id,
                            title = transaction.note.orEmpty().ifBlank { categoryName },
                            categoryName = categoryName,
                            dateLabel = transaction.transactionDate.format(
                                DateTimeFormatter.ofPattern(
                                    "dd MMMM yyyy",
                                    Locale("id", "ID")
                                )
                            ),
                            amount = transaction.amount,
                            type = when (transaction.type) {
                                TransactionType.INCOME -> HomeTransactionType.INCOME
                                TransactionType.EXPENSE -> HomeTransactionType.EXPENSE
                                TransactionType.SAVING -> HomeTransactionType.SAVING
                            },
                            savingGoalId = transaction.savingGoalId
                        )
                    },
                    savingGoals = goals,
                    isLoading = false
                )
            }.catch { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message ?: "Gagal memuat data Home"
                    )
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun previousMonth() {
        selectedMonth.update { it.minusMonths(1) }
    }

    fun nextMonth() {
        selectedMonth.update {
            val next = it.plusMonths(1)
            if (next.isAfter(YearMonth.now())) it else next
        }
    }
}