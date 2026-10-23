package com.example.cashpeer.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.cashpeer.feature.statistics.presentation.ExpenseCategoryUi
import com.example.cashpeer.feature.statistics.presentation.MonthlyTrendUi
import com.example.cashpeer.feature.statistics.presentation.SavingGoalProgressUi
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

private val rupiahFormatter: NumberFormat =
    NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

private fun formatRupiah(amount: Long): String =
    rupiahFormatter.format(amount)

@Composable
fun StatisticsSectionCard(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            content()
        }
    }

}

@Composable
fun StatisticsSummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    amount: Long
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = formatRupiah(amount),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun ExpenseRatioCard(
    expensePercentage: Float,
    remainingPercentage: Float,
    remainingAmount: Float
) {
    StatisticsSectionCard(title = "Perbandingan Pemasukan") {
        Text(
            text = "Pengeluaran menggunakan ${expensePercentage.toInt()}% dari pemasukkan",
            style = MaterialTheme.typography.bodyMedium
        )

        LinearProgressIndicator(
            progress = {(expensePercentage/100f).coerceIn(0f,1f)},
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )

        Text(
            text = if(remainingAmount >=0L){
                "Sisa setelah pengeluaran: ${formatRupiah((remainingAmount.toLong()))} " + "(${remainingPercentage.toInt()}% dari pemasukan)"
            }else{
                "Kekurangan: ${formatRupiah(-remainingAmount.toLong())} " + "(${remainingPercentage.toInt()}% dari pemasukan"
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ExpenseCategoryCard(
    category: ExpenseCategoryUi?
) {
    StatisticsSectionCard(title = "Kategori pengeluaran terbesar") {
        if(category == null){
            Text(
                text = "Belum ada data pengeluaran bulan ini.",
                style = MaterialTheme.typography.bodyMedium
            )
        }else{
            Text(
                text = category.categoryName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = formatRupiah(category.amount),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${category.percentage.toInt()}% dari total pengeluaran",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MonthlyTrendCard(
    trends: List<MonthlyTrendUi>
) {
    StatisticsSectionCard(
        title = "Tren 6 bulan"
    ) {
        if(trends.isEmpty()){
            Text("Belum ada data transaksi.")
        }else{
            val maxAmount = trends.maxOf{
                maxOf(it.income, it.expense, it.saving)
            }.coerceAtLeast(1L)

            trends.forEach { trend ->
                val monthLabel = trend.month.format(
                    DateTimeFormatter.ofPattern("MMMM yyyy", Locale("id", "ID"))
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = monthLabel,
                        style = MaterialTheme.typography.titleSmall
                    )
                    TrendBar(
                        label = "Pemasukan",
                        amount = trend.income,
                        maxAmount =maxAmount
                    )
                    TrendBar(
                        label = "Pengeluaran",
                        amount = trend.expense,
                        maxAmount =maxAmount
                    )
                    TrendBar(
                        label = "Tabungan",
                        amount = trend.saving,
                        maxAmount = maxAmount
                    )
                }
            }
        }
    }
}

@Composable
private fun TrendBar(
    label: String,
    amount: Long,
    maxAmount: Long
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = formatRupiah(amount),
                style = MaterialTheme.typography.bodySmall
            )
        }

        LinearProgressIndicator(
            progress = {
                (amount.toFloat() / maxAmount.toFloat()).coerceIn(0f, 1f)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        )
    }
}

@Composable
fun SavingGoalProgressCard(
    goals: List<SavingGoalProgressUi>
) {
    StatisticsSectionCard(title = "Progres target tabungan") {
        if(goals.isEmpty()){
            Text(
                text = "Belum ada target tabungan.",
                style = MaterialTheme.typography.bodyMedium
            )
        }else{
            goals.forEach { goal ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = goal.goalName,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = "${formatRupiah(goal.savedAmount)} dari " + formatRupiah(goal.targetAmount),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    LinearProgressIndicator(
                        progress = {goal.progress},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                    )
                    Text(
                        text = "${(goal.progress * 100).toInt()}% tercapai",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseCategoryDonutChart(
    categories: List<ExpenseCategoryUi>,
    totalExpense: Long,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val totalColor = MaterialTheme.colorScheme.onSurface
    val supportingColor = MaterialTheme.colorScheme.onSurfaceVariant

    StatisticsSectionCard(
        title = "Pengeluaran per kategori",
        modifier = modifier
    ) {
        if (totalExpense <= 0L || categories.isEmpty()) {
            Text(
                text = "Belum ada pengeluaran pada bulan ini.",
                color = supportingColor,
                style = MaterialTheme.typography.bodyMedium
            )
            return@StatisticsSectionCard
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.size(190.dp)
            ) {
                val strokeWidth = 28.dp.toPx()
                val diameter = size.minDimension - strokeWidth
                val topLeft = Offset(
                    (size.width - diameter) / 2f,
                    (size.height - diameter) / 2f
                )
                val arcSize = Size(diameter, diameter)

                drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )

                var startAngle = -90f

                categories.forEachIndexed { index, category ->
                    val sweepAngle =
                        category.amount.toFloat() /
                                totalExpense.toFloat() * 360f

                    if (sweepAngle > 0f) {
                        drawArc(
                            color = chartColors[index % chartColors.size],
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(
                                width = strokeWidth,
                                cap = StrokeCap.Butt
                            )
                        )
                    }

                    startAngle += sweepAngle
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Total pengeluaran",
                    color = supportingColor,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = formatChartRupiah(totalExpense),
                    color = totalColor,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        categories.forEachIndexed { index, category ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            chartColors[index % chartColors.size],
                            CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = category.categoryName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = formatChartRupiah(category.amount),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${category.percentage.toInt()}%",
                        color = supportingColor,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

private val chartColors = listOf(
    Color(0xFF6750A4),
    Color(0xFF00897B),
    Color(0xFFFFA000),
    Color(0xFF4285F4),
    Color(0xFFE57373),
    Color(0xFF7E57C2),
    Color(0xFF78909C),
    Color(0xFF66BB6A)
)

private fun formatChartRupiah(amount: Long): String =
    NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }.format(amount)