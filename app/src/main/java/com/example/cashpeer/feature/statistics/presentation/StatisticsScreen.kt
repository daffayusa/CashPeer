package com.example.cashpeer.feature.statistics.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cashpeer.core.ui.components.ExpenseCategoryCard
import com.example.cashpeer.core.ui.components.ExpenseCategoryDonutChart
import com.example.cashpeer.core.ui.components.ExpenseRatioCard
import com.example.cashpeer.core.ui.components.MonthlyTrendCard
import com.example.cashpeer.core.ui.components.SavingGoalProgressCard
import com.example.cashpeer.core.ui.components.StatisticsSummaryCard
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun StatisticsScreen(
    uiState: StatisticsUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onCurrentMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(
            bottom = 110.dp
        )
    ) {
        item {
            MonthSelector(
                month = uiState.selectedMonth,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onCurrentMonth = onCurrentMonth
            )
        }

//        item {
//            Text(
//                text = "Ringkasan keuangan",
//                style = MaterialTheme.typography.titleLarge
//            )
//        }

//        item {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                StatisticsSummaryCard(
//                    title = "Pemasukan",
//                    amount = uiState.totalIncome,
//                    modifier = Modifier.weight(1f)
//                )
//                StatisticsSummaryCard(
//                    title = "Pengeluaran",
//                    amount = uiState.totalExpense,
//                    modifier = Modifier.weight(1f)
//                )
//            }
//        }

//        item {
//            StatisticsSummaryCard(
//                title = "Tabungan bulan ini",
//                amount = uiState.totalSaving,
//                modifier = Modifier.fillMaxWidth()
//            )
//        }

        item {
//            ExpenseCategoryCard(
//                category = uiState.highestExpenseCategory
//            )
            ExpenseCategoryDonutChart(
                categories = uiState.expenseCategories,
                totalExpense = uiState.totalExpense
            )
        }

        item {
            ExpenseRatioCard(
                expensePercentage = uiState.expensePercentage,
                remainingPercentage = uiState.remainingPercentage,
                remainingAmount = uiState.remainingAmount.toFloat()
            )
        }

        item {
            MonthlyTrendCard(
                trends = uiState.monthlyTrends
            )
        }

        item {
            SavingGoalProgressCard(
                goals = uiState.savingGoals
            )
        }
    }
}


@Composable
private fun MonthSelector(
    month: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onCurrentMonth: () -> Unit
) {
    val currentMonth = YearMonth.now()
    val monthLabel = month.format(
        DateTimeFormatter.ofPattern("MMMM yyyy", Locale("id", "ID"))
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text(
            text = "Statistik Keuangan",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onPreviousMonth) {
                Text("‹ Sebelumnya")
            }

            Text(
                text = monthLabel,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp)
            )

            TextButton(
                onClick = onNextMonth,
                enabled = month < currentMonth
            ) {
                Text("Berikutnya ›")
            }
        }

        if (month != currentMonth) {
            Button(
                onClick = onCurrentMonth,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Kembali ke bulan ini")
            }
        }
    }
}