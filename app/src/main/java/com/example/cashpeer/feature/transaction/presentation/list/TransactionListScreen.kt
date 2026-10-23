package com.example.cashpeer.feature.transaction.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cashpeer.core.ui.components.CpSegmentedButton
import com.example.cashpeer.core.ui.components.CpTransactionFilterItem
import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import java.nio.file.WatchEvent
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    onBack: () -> Unit,
    onAddTransaction: () -> Unit,
    viewModel: TransactionListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = "Daftar Transaksi",
//                        fontWeight = FontWeight.SemiBold
//                    )
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.surface
//                ),
//                windowInsets = TopAppBarDefaults.windowInsets
//            )
//        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTransaction, shape = CircleShape, containerColor = MaterialTheme.colorScheme.primary, modifier = Modifier . offset(x = -8.dp, y = -24.dp)) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah transaksi"
                )

            }
        }
    ) { innerPadding ->
        Text(
            text = "Daftar Transaksi",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 18.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                    )
                }

                state.error != null -> {
                    Text(
                        text = state.error ?: "Terjadi kesalahan",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(androidx.compose.ui.Alignment.Center)
                            .padding(24.dp)
                    )
                }

                else -> {
                    val filteredTransactions = state.transactions.filter { transaction ->
                        state.selectedType == null ||
                                transaction.type == state.selectedType
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                    ) {

                        val filterOptions = listOf(
                            "Semua",
                            "Penghasilan",
                            "Pengeluaran",
                            "Tabungan"
                        )

                        val selectedIndex = when (state.selectedType) {
                            null -> 0
                            TransactionType.INCOME -> 1
                            TransactionType.EXPENSE -> 2
                            TransactionType.SAVING -> 3
                        }

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(filterOptions) { index, label ->
                                CpTransactionFilterItem(
                                    text = label,
                                    selected = selectedIndex == index,
                                    onClick = {
                                        viewModel.setTransactionType(
                                            when (index) {
                                                1 -> TransactionType.INCOME
                                                2 -> TransactionType.EXPENSE
                                                3 -> TransactionType.SAVING
                                                else -> null
                                            }
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (filteredTransactions.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Text(
                                    text = if (state.transactions.isEmpty()) {
                                        "Belum ada transaksi"
                                    } else {
                                        "Tidak ada transaksi untuk filter ini"
                                    },
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(
                                    items = filteredTransactions,
                                    key = { it.id }
                                ) { transaction ->
                                    val categoryName = state.categories
                                        .firstOrNull {
                                            it.id == transaction.categoryId
                                        }
                                        ?.name

                                    TransactionItem(
                                        transaction = transaction,
                                        categoryName = categoryName
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    }

}




@Composable
private fun TransactionItem(
    transaction: Transaction,
    categoryName: String?
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val typeLabel = when (transaction.type){
        TransactionType.INCOME -> "Penghasilan"
        TransactionType.EXPENSE -> "Pengeluaran"
        TransactionType.SAVING -> "Tabungan"
    }
    val amountPrefix = when(transaction.type){
        TransactionType.INCOME -> "+"
        TransactionType.EXPENSE,
        TransactionType.SAVING -> "-"
    }
    val amountColor = if(isIncome){
        MaterialTheme.colorScheme.primary
    }else{
        MaterialTheme.colorScheme.onSurface
    }
    val formattedAmount = NumberFormat
        .getCurrencyInstance(Locale("id","ID"))
        .format(transaction.amount)

    val formattedDate = transaction.transactionDate.format(
        DateTimeFormatter.ofPattern("dd MM yyyy", Locale("id", "ID"))
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (isIncome) {
                    Icons.Default.ArrowDownward
                } else {
                    Icons.Default.ArrowUpward
                },
                contentDescription = typeLabel,
                tint = amountColor
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = categoryName
                        ?: transaction.note?.takeIf { it.isNotBlank() }
                        ?: typeLabel,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "$typeLabel • $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = amountPrefix + formattedAmount,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = amountColor
            )
        }
    }
}

@Composable
private fun EmptyTransactionState() {
    Text(
        text = "Belum ada transaksi"
    )
}