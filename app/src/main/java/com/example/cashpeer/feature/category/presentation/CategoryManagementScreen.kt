package com.example.cashpeer.feature.category.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cashpeer.feature.category.domain.model.Category
import com.example.cashpeer.feature.transaction.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: CategoryManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kelola Kategori",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Tambah, ubah, atau hapus kategori transaksimu.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = viewModel::showAddDialog,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Tambah Kategori", fontWeight = FontWeight.SemiBold)
            }

            when {
                uiState.isLoading -> {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.categories.isEmpty() -> {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Belum ada kategori.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    val incomeCategories = uiState.categories.filter { it.type == TransactionType.INCOME }
                    val expenseCategories = uiState.categories.filter { it.type == TransactionType.EXPENSE }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        if (incomeCategories.isNotEmpty()) {
                            item {
                                Text(
                                    "Pemasukan",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                            }
                            items(incomeCategories, key = { it.id }) { category ->
                                CategoryItem(
                                    category = category,
                                    onEdit = { viewModel.showEditDialog(category) },
                                    onDelete = { viewModel.confirmDelete(category) }
                                )
                            }
                        }
                        if (expenseCategories.isNotEmpty()) {
                            item {
                                Text(
                                    "Pengeluaran",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                                )
                            }
                            items(expenseCategories, key = { it.id }) { category ->
                                CategoryItem(
                                    category = category,
                                    onEdit = { viewModel.showEditDialog(category) },
                                    onDelete = { viewModel.confirmDelete(category) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog Tambah/Edit
    if (uiState.showDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDialog,
            title = {
                Text(if (uiState.editingCategory == null) "Tambah Kategori" else "Edit Kategori")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = uiState.dialogName,
                        onValueChange = viewModel::setDialogName,
                        label = { Text("Nama kategori") },
                        placeholder = { Text("Contoh: Hiburan, Kesehatan") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSavingDialog
                    )
                    Text("Tipe", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = uiState.dialogType == TransactionType.INCOME,
                            onClick = { viewModel.setDialogType(TransactionType.INCOME) },
                            label = { Text("Pemasukan") },
                            enabled = !uiState.isSavingDialog
                        )
                        FilterChip(
                            selected = uiState.dialogType == TransactionType.EXPENSE,
                            onClick = { viewModel.setDialogType(TransactionType.EXPENSE) },
                            label = { Text("Pengeluaran") },
                            enabled = !uiState.isSavingDialog
                        )
                    }
                    uiState.dialogError?.let {
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::saveCategory,
                    enabled = !uiState.isSavingDialog
                ) {
                    if (uiState.isSavingDialog) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Simpan")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = viewModel::dismissDialog,
                    enabled = !uiState.isSavingDialog
                ) {
                    Text("Batal")
                }
            }
        )
    }

    // Dialog Konfirmasi Hapus
    uiState.deletingCategory?.let { category ->
        AlertDialog(
            onDismissRequest = viewModel::dismissDelete,
            title = { Text("Hapus Kategori?") },
            text = { Text("Kategori \"${category.name}\" akan dihapus. Apakah kamu yakin?") },
            confirmButton = {
                TextButton(onClick = viewModel::deleteCategory) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDelete) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
