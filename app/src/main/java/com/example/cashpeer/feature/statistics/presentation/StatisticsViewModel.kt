
package com.example.cashpeer.feature.statistics.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cashpeer.feature.category.domain.usecase.GetCategoryUseCase
import com.example.cashpeer.feature.savinggoal.domain.model.SavingGoal
import com.example.cashpeer.feature.savinggoal.domain.usecase.ObserveSavingGoalUseCase
import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import com.example.cashpeer.feature.transaction.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    getTransactionsUseCase: GetTransactionsUseCase,
    getCategoryUseCase: GetCategoryUseCase,
    observeSavingGoalUseCase: ObserveSavingGoalUseCase
) : ViewModel() {

    companion object {
        private const val USER_ID = 1L
        private const val TREND_MONTHS = 6
    }

    private val selectedMonth = MutableStateFlow(YearMonth.now())

    private val transactions = getTransactionsUseCase(USER_ID)
    private val categories = getCategoryUseCase(
        USER_ID,
        TransactionType.EXPENSE
    )
    private val savingGoals = observeSavingGoalUseCase(USER_ID)

    val uiState = combine(
        transactions,
        categories,
        savingGoals,
        selectedMonth
    ) { transactionList, categoryList, goalList, month ->
        createUiState(
            transactions = transactionList,
            categories = categoryList,
            goals = goalList,
            month = month
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatisticsUiState()
    )

    fun previousMonth() {
        selectedMonth.value = selectedMonth.value.minusMonths(1)
    }

    fun nextMonth() {
        val next = selectedMonth.value.plusMonths(1)

        if (next <= YearMonth.now()) {
            selectedMonth.value = next
        }
    }

    fun selectCurrentMonth() {
        selectedMonth.value = YearMonth.now()
    }

    private fun createUiState(
        transactions: List<Transaction>,
        categories: List<com.example.cashpeer.feature.category.domain.model.Category>,
        goals: List<SavingGoal>,
        month: YearMonth
    ): StatisticsUiState {
        val startDate = month.atDay(1)
        val endDate = month.plusMonths(1).atDay(1)

        val monthTransactions = transactions.filter {
            !it.transactionDate.isBefore(startDate) &&
                    it.transactionDate.isBefore(endDate)
        }

        val income = monthTransactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val expense = monthTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        val saving = monthTransactions
            .filter { it.type == TransactionType.SAVING }
            .sumOf { it.amount }

        val expensePercentage = if (income > 0L) {
            expense.toDouble()
                .div(income.toDouble())
                .times(100.0)
                .toFloat()
        } else {
            0f
        }

        val remainingAmount = income - expense

        val remainingPercentage = if (income > 0L) {
            ((income - expense).toDouble() / income * 100.0)
                .toFloat()
        } else {
            0f
        }

        val categoryNames = categories.associate {
            it.id to it.name
        }

        val expenseCategories = monthTransactions
            .filter {
                it.type == TransactionType.EXPENSE &&
                        it.categoryId != null
            }
            .groupBy { it.categoryId!! }
            .map { (categoryId, items) ->
                val amount = items.sumOf { it.amount }

                ExpenseCategoryUi(
                    categoryId = categoryId,
                    categoryName = categoryNames[categoryId]
                        ?: "Kategori lainnya",
                    amount = amount,
                    percentage = if (expense > 0L) {
                        amount.toDouble()
                            .div(expense.toDouble())
                            .times(100.0)
                            .toFloat()
                    } else {
                        0f
                    }
                )
            }
            .sortedByDescending { it.amount }

        val monthlyTrends = (TREND_MONTHS - 1 downTo 0)
            .map { offset ->
                val trendMonth = month.minusMonths(offset.toLong())
                val start = trendMonth.atDay(1)
                val end = trendMonth.plusMonths(1).atDay(1)

                val items = transactions.filter {
                    !it.transactionDate.isBefore(start) &&
                            it.transactionDate.isBefore(end)
                }

                MonthlyTrendUi(
                    month = trendMonth,
                    income = items
                        .filter { it.type == TransactionType.INCOME }
                        .sumOf { it.amount },
                    expense = items
                        .filter { it.type == TransactionType.EXPENSE }
                        .sumOf { it.amount },
                    saving = items
                        .filter { it.type == TransactionType.SAVING }
                        .sumOf { it.amount }
                )
            }

        val savingGoalProgress = goals.map { goal ->
            val saved = transactions
                .filter {
                    it.type == TransactionType.SAVING &&
                            it.savingGoalId == goal.id
                }
                .sumOf { it.amount }

            SavingGoalProgressUi(
                goalId = goal.id,
                goalName = goal.name,
                savedAmount = saved,
                targetAmount = goal.targetAmount
            )
        }

        return StatisticsUiState(
            selectedMonth = month,
            totalIncome = income,
            totalExpense = expense,
            totalSaving = saving,
            expensePercentage = expensePercentage,
            remainingPercentage = remainingPercentage,
            remainingAmount = remainingAmount,
            highestExpenseCategory = expenseCategories.firstOrNull(),
            expenseCategories = expenseCategories,
            monthlyTrends = monthlyTrends,
            savingGoals = savingGoalProgress,
            isLoading = false
        )
    }
}
