package com.example.cashpeer.feature.statistics.presentation

import java.time.YearMonth

data class StatisticsUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L,
    val totalSaving: Long = 0L,
    val expensePercentage: Float = 0f,
    val remainingPercentage: Float = 0f,
    val remainingAmount: Long = 0L,
    val highestExpenseCategory: ExpenseCategoryUi? = null,
    val expenseCategories: List<ExpenseCategoryUi> = emptyList(),
    val monthlyTrends: List<MonthlyTrendUi> = emptyList(),
    val savingGoals: List<SavingGoalProgressUi> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class ExpenseCategoryUi(
    val categoryId: Long,
    val categoryName: String,
    val amount: Long,
    val percentage: Float
)

data class MonthlyTrendUi(
    val month: YearMonth,
    val income: Long,
    val expense: Long,
    val saving: Long
)

data class SavingGoalProgressUi(
    val goalId: Long,
    val goalName: String,
    val savedAmount: Long,
    val targetAmount: Long
) {
    val progress: Float
        get() = if (targetAmount > 0L) {
            (savedAmount.toDouble() / targetAmount)
                .coerceIn(0.0, 1.0)
                .toFloat()
        } else {
            0f
        }

    val remainingAmount: Long
        get() = (targetAmount - savedAmount).coerceAtLeast(0L)
}