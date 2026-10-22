package com.example.cashpeer.feature.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

private val HomeGreen = Color(0xFF2FB66A)
private val HomeDarkGreen = Color(0xFF07843F)
private val HomeLightGreen = Color(0xFFD8F5E4)
private val HomeMutedGreen = Color(0xFFF0FBF4)
private val HomeRed = Color(0xFFFF3348)
private val HomeMutedText = Color(0xFF858585)

@Composable
fun HomeScreen(
    uiState: HomeUiState = HomeUiState(),
    onAddTransaction: () -> Unit,
    onViewTransactions: () -> Unit,
    onViewSavingGoals: () -> Unit,
    onNotificationClick: () -> Unit = {},
    onSavingGoalClick: (Long) -> Unit = {},
    onMonthClick: () -> Unit = {},
    onPreviousMonth: () -> Unit = {},
    onNextMonth: () -> Unit = {}
) {
    var showMonthDialog by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F5)),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            HomeHeader(
                userName = uiState.userName,
                onNotificationClick = onNotificationClick
            )
        }

        item {
            HomeBalanceCard(
                balance = uiState.totalBalance,
                income = uiState.monthlyIncome,
                expense = uiState.monthlyExpense
            )
        }

        item {
            MonthlySummaryCard(
                monthLabel = uiState.monthLabel,
                income = uiState.monthlyIncome,
                expense = uiState.monthlyExpense,
                balance = uiState.totalBalance,
                onMonthClick = {showMonthDialog = true}
            )
        }

        item {
            SavingGoalsCard(
                goals = uiState.savingGoals,
                onViewAll = onViewSavingGoals,
                onGoalClick = onSavingGoalClick
            )
        }

        item {
            RecentTransactionsCard(
                transactions = uiState.recentTransactions,
                onViewAll = onViewTransactions
            )
        }

        item {
            AddTransactionButton(onClick = onAddTransaction)
        }
    }
    if(showMonthDialog){
        AlertDialog(
            onDismissRequest = {
                showMonthDialog = false
            },
            title = {
                Text(text = "Pilih bulan")
            },
            text ={
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onPreviousMonth
                    ) {
                        Icon(
                            Icons.Default.ChevronLeft,
                            contentDescription = "Bulan sebelumnya"
                        )
                    }

                    Text(
                        uiState.monthLabel.ifBlank { "Bulan ini" },
                        fontWeight = FontWeight.SemiBold
                    )

                    IconButton(
                        onClick = onNextMonth
                    ) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "Bulan berikutnya"
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showMonthDialog = false
                    }
                ) {
                    Text("Selesai")
                }
            }
        )
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(HomeLightGreen),
            contentAlignment = Alignment.Center
        ) {
            Text("👋", fontSize = 25.sp)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Halo,",
                style = MaterialTheme.typography.bodyMedium,
                color = HomeMutedText
            )
            Text(
                text = userName.ifBlank { "Pengguna CashPeer" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        IconButton(onClick = onNotificationClick) {
            Icon(
                imageVector = Icons.Default.NotificationsNone,
                contentDescription = "Notifikasi",
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun HomeBalanceCard(
    balance: Long,
    income: Long,
    expense: Long
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF45C47B),
                        Color(0xFF149653)
                    )
                )
            )
            .padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AccountBalanceWallet,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "TOTAL SALDO",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = formatRupiah(balance),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x22000000))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Pemasukan", color = Color.White)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        formatRupiah(income),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("Pengeluaran", color = Color.White)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        formatRupiah(expense),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthlySummaryCard(
    monthLabel: String,
    income: Long,
    expense: Long,
    balance: Long,
    onMonthClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = HomeDarkGreen
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    "Ringkasan Bulan Ini",
                    modifier = Modifier.weight(1f),
                    color = HomeDarkGreen,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                TextButton(
                    onClick = onMonthClick,
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text(
                        monthLabel.ifBlank { "Bulan ini" },
                        color = HomeMutedText,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SummaryValue(
                    modifier = Modifier.weight(1f),
                    label = "Pemasukan",
                    amount = income,
                    color = HomeGreen,
                    icon = {
                        Icon(
                            Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = HomeGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
                SummaryValue(
                    modifier = Modifier.weight(1f),
                    label = "Pengeluaran",
                    amount = expense,
                    color = HomeRed,
                    icon = {
                        Icon(
                            Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = HomeRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
                SummaryValue(
                    modifier = Modifier.weight(1f),
                    label = "Saldo",
                    amount = balance,
                    color = HomeDarkGreen,
                    icon = {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = HomeDarkGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SummaryValue(
    modifier: Modifier = Modifier,
    label: String,
    amount: Long,
    color: Color,
    icon: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(HomeMutedGreen)
            .padding(horizontal = 7.dp, vertical = 9.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon()
            Spacer(Modifier.width(3.dp))
            Text(
                text = label,
                color = HomeMutedText,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(5.dp))

        Text(
            text = formatRupiah(amount),
            color = color,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SavingGoalsCard(
    goals: List<HomeSavingGoalUi>,
    onViewAll: () -> Unit,
    onGoalClick: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(HomeLightGreen)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.TrendingUp,
                contentDescription = null,
                tint = HomeDarkGreen
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "Target Tabungan",
                modifier = Modifier.weight(1f),
                color = HomeDarkGreen,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Lihat Semua",
                color = HomeMutedText,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.clickable(onClick = onViewAll)
            )
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Lihat semua target",
                tint = HomeMutedText,
                modifier = Modifier
                    .size(15.dp)
                    .clickable(onClick = onViewAll)
            )
        }

        if (goals.isEmpty()) {
            Text(
                text = "Belum ada target tabungan. Yuk, buat target pertamamu!",
                style = MaterialTheme.typography.bodyMedium,
                color = HomeDarkGreen
            )
        } else {
            goals.take(2).forEach { goal ->
                SavingGoalPreview(
                    goal = goal,
                    onClick = { onGoalClick(goal.id) }
                )
            }
        }
    }
}


@Composable
private fun SavingGoalPreview(
    goal: HomeSavingGoalUi,
    onClick: () -> Unit
) {
    val remainingAmount = (goal.targetAmount - goal.savedAmount)
        .coerceAtLeast(0L)

    val isCompleted = goal.targetAmount > 0L &&
            goal.savedAmount >= goal.targetAmount

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(HomeLightGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Laptop,
                contentDescription = null,
                tint = HomeDarkGreen,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = goal.name,
                color = HomeDarkGreen,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${formatRupiah(goal.savedAmount)} / " +
                        formatRupiah(goal.targetAmount),
                color = HomeMutedText,
                style = MaterialTheme.typography.labelSmall
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LinearProgressIndicator(
                    progress = { goal.progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(7.dp)
                        .clip(CircleShape),
                    color = HomeGreen,
                    trackColor = Color(0xFFE3E3E3)
                )

                Text(
                    text = "${(goal.progress * 100).toInt()}%",
                    color = HomeDarkGreen,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            if (isCompleted) {
                Text(
                    text = "Target tercapai!",
                    color = HomeGreen,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelSmall
                )
            } else {
                Text(
                    text = "Kurang ${formatRupiah(remainingAmount)}",
                    color = HomeMutedText,
                    style = MaterialTheme.typography.labelSmall
                )

                goal.estimatedCompletion?.let { estimate ->
                    Text(
                        text = "Estimasi tercapai: $estimate",
                        color = HomeMutedText,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}


@Composable
private fun RecentTransactionsCard(
    transactions: List<HomeTransactionUi>,
    onViewAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Transaksi Terakhir",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    "Lihat Semua",
                    color = HomeMutedText,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.clickable(onClick = onViewAll)
                )
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = "Lihat semua transaksi",
                    tint = HomeMutedText,
                    modifier = Modifier.size(18.dp)
                )
            }

            if (transactions.isEmpty()) {
                Text(
                    "Belum ada transaksi.",
                    color = HomeMutedText,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                transactions.take(4).forEach { transaction ->
                    RecentTransactionRow(transaction)
                }
            }
        }
    }
}

@Composable
private fun RecentTransactionRow(
    transaction: HomeTransactionUi
) {
    val amountColor = when (transaction.type) {
        HomeTransactionType.INCOME -> HomeGreen
        HomeTransactionType.EXPENSE -> HomeRed
        HomeTransactionType.SAVING -> Color(0xFF4782E8)
    }

    val amountPrefix = when (transaction.type) {
        HomeTransactionType.INCOME -> "+ "
        HomeTransactionType.EXPENSE -> "- "
        HomeTransactionType.SAVING -> ""
    }

    val iconBackground = when (transaction.type) {
        HomeTransactionType.INCOME -> Color(0xFFD9F9E7)
        HomeTransactionType.EXPENSE -> Color(0xFFFFE5C2)
        HomeTransactionType.SAVING -> Color(0xFFDDE8FF)
    }

    val icon = when (transaction.type) {
        HomeTransactionType.INCOME -> Icons.Default.TrendingUp
        HomeTransactionType.EXPENSE -> Icons.Default.ShoppingBag
        HomeTransactionType.SAVING -> Icons.Default.Savings
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = amountColor
            )
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.title,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "${transaction.categoryName} • ${transaction.dateLabel}",
                color = HomeMutedText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = amountPrefix + formatRupiah(transaction.amount),
            color = amountColor,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1
        )
    }
}

@Composable
private fun AddTransactionButton(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(50))
            .background(
                Brush.horizontalGradient(
                    listOf(HomeGreen, Color(0xFF209F59))
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "+",
                color = HomeGreen,
                fontSize = 30.sp,
                fontWeight = FontWeight.Light
            )
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Tambah Transaksi",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Catat pemasukan atau pengeluaranmu",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall
            )
        }

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.White
        )
    }
}

private fun formatRupiah(amount: Long): String {
    return NumberFormat.getCurrencyInstance(
        Locale("id", "ID")
    ).format(amount).replace(",00", "")
}