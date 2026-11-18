package com.example.cashpeer.feature.transaction.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cashpeer.core.ui.components.CpTransactionFilterItem
import com.example.cashpeer.feature.transaction.domain.model.Transaction
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    onBack: () -> Unit,
    onAddTransaction: () -> Unit,
    onEditTransaction: (Long) -> Unit = {},
    viewModel: TransactionListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var transactionToDelete by remember {
        mutableStateOf<Transaction?>(null)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransaction,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 76.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah transaksi"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header & Search
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Daftar Transaksi",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Search Bar
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = viewModel::setSearchQuery,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari catatan atau nominal...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cari",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Hapus pencarian",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                // Filter Tipe Transaksi
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

                // Filter Kategori (jika relevan)
                val relevantCategories = when (state.selectedType) {
                    TransactionType.INCOME -> state.categories.filter { it.type == TransactionType.INCOME }
                    TransactionType.EXPENSE -> state.categories.filter { it.type == TransactionType.EXPENSE }
                    null -> state.categories
                    TransactionType.SAVING -> emptyList()
                }

                if (relevantCategories.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = state.selectedCategoryId == null,
                                onClick = { viewModel.setSelectedCategoryId(null) },
                                label = { Text("Semua Kategori") }
                            )
                        }
                        items(relevantCategories, key = { it.id }) { cat ->
                            FilterChip(
                                selected = state.selectedCategoryId == cat.id,
                                onClick = {
                                    if (state.selectedCategoryId == cat.id) {
                                        viewModel.setSelectedCategoryId(null)
                                    } else {
                                        viewModel.setSelectedCategoryId(cat.id)
                                    }
                                },
                                label = { Text(cat.name) }
                            )
                        }
                    }
                }
            }

            // Body list
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 18.dp)
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    state.error != null -> {
                        Text(
                            text = state.error ?: "Terjadi kesalahan",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp)
                        )
                    }

                    else -> {
                        val query = state.searchQuery.trim().lowercase()
                        val filteredTransactions = state.transactions.filter { transaction ->
                            // 1. Filter type
                            val matchesType = state.selectedType == null ||
                                    transaction.type == state.selectedType

                            // 2. Filter category
                            val matchesCategory = state.selectedCategoryId == null ||
                                    transaction.categoryId == state.selectedCategoryId

                            // 3. Search query
                            val categoryName = state.categories
                                .firstOrNull { it.id == transaction.categoryId }
                                ?.name
                                .orEmpty()
                                .lowercase()
                            val note = transaction.note.orEmpty().lowercase()
                            val amountStr = transaction.amount.toString()

                            val matchesSearch = query.isEmpty() ||
                                    note.contains(query) ||
                                    categoryName.contains(query) ||
                                    amountStr.contains(query)

                            matchesType && matchesCategory && matchesSearch
                        }

                        if (filteredTransactions.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (state.transactions.isEmpty()) {
                                        "Belum ada transaksi"
                                    } else {
                                        "Tidak ada transaksi yang cocok"
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(top = 4.dp, bottom = 120.dp),
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
                                        categoryName = categoryName,
                                        onEdit = { onEditTransaction(transaction.id) },
                                        onDelete = {
                                            transactionToDelete = transaction
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    transactionToDelete?.let { transaction ->
        AlertDialog(
            onDismissRequest = {
                transactionToDelete = null
            },
            title = {
                Text("Hapus Transaksi?")
            },
            text = {
                Text("Transaksi ini akan dihapus secara permanen. Apakah kamu yakin?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTransaction(transaction)
                        transactionToDelete = null
                    }
                ) {
                    Text(
                        text = "Hapus",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        transactionToDelete = null
                    }
                ) {
                    Text("Batal")
                }
            }
        )
    }

    state.deleteError?.let { error ->
        AlertDialog(
            onDismissRequest = {
                viewModel.clearDeleteError()
            },
            title = {
                Text("Gagal Menghapus")
            },
            text = {
                Text(error)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearDeleteError()
                    }
                ) {
                    Text("Tutup")
                }
            }
        )
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    categoryName: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val isExpense = transaction.type == TransactionType.EXPENSE

    val typeLabel = when (transaction.type) {
        TransactionType.INCOME -> "Penghasilan"
        TransactionType.EXPENSE -> "Pengeluaran"
        TransactionType.SAVING -> "Tabungan"
    }

    val amountPrefix = when (transaction.type) {
        TransactionType.INCOME -> "+ "
        TransactionType.EXPENSE -> "- "
        TransactionType.SAVING -> ""
    }

    val amountColor = when (transaction.type) {
        TransactionType.INCOME -> Color(0xFF2FB66A)
        TransactionType.EXPENSE -> Color(0xFFFF3348)
        TransactionType.SAVING -> Color(0xFF4782E8)
    }

    val iconBgColor = when (transaction.type) {
        TransactionType.INCOME -> Color(0xFFD8F5E4)
        TransactionType.EXPENSE -> Color(0xFFFFE3E7)
        TransactionType.SAVING -> Color(0xFFDDE8FF)
    }

    val iconVector = when (transaction.type) {
        TransactionType.INCOME -> Icons.Default.ArrowDownward
        TransactionType.EXPENSE -> Icons.Default.ArrowUpward
        TransactionType.SAVING -> Icons.Default.Savings
    }

    val formattedAmount = NumberFormat
        .getCurrencyInstance(Locale("id", "ID"))
        .format(transaction.amount)
        .replace(",00", "")

    val formattedDate = transaction.transactionDate.format(
        DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id", "ID"))
    )

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
            }
            false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFFFE3E7))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = typeLabel,
                        tint = amountColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = transaction.note?.takeIf { it.isNotBlank() }
                            ?: categoryName
                            ?: typeLabel,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "${categoryName ?: typeLabel} • $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = amountPrefix + formattedAmount,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )

                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit transaksi",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus transaksi",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
