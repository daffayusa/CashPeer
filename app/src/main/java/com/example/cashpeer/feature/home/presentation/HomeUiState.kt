package com.example.cashpeer.feature.home.presentation

data class HomeUiState(
    val userName: String = "",
    val totalBalance: Long = 0L,
    val monthlyIncome: Long = 0L,
    val monthlyExpense: Long = 0L,
    val monthLabel: String = "",
    val savingGoals: List<HomeSavingGoalUi> = emptyList(),
    val recentTransactions: List<HomeTransactionUi> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class HomeSavingGoalUi(
    val id: Long,
    val name: String,
    val savedAmount: Long,
    val targetAmount: Long,
    val estimatedCompletion: String? = null
) {
    val progress: Float
        get() = if (targetAmount > 0L) {
            (savedAmount.toDouble() / targetAmount)
                .coerceIn(0.0, 1.0)
                .toFloat()
        } else {
            0f
        }
}

data class HomeTransactionUi(
    val id: Long,
    val title: String,
    val categoryName: String,
    val dateLabel: String,
    val amount: Long,
    val type: HomeTransactionType
)

enum class HomeTransactionType {
    INCOME,
    EXPENSE,
    SAVING
}