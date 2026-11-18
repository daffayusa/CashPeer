package com.example.cashpeer.feature.transaction.presentation.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cashpeer.core.ui.components.CpButton
import com.example.cashpeer.core.ui.components.CpDatePicker
import com.example.cashpeer.core.ui.components.CpDropdown
import com.example.cashpeer.core.ui.components.CpRupiahTextField
import com.example.cashpeer.core.ui.components.CpSegmentedButton
import com.example.cashpeer.core.ui.components.CpTextField
import com.example.cashpeer.feature.transaction.domain.model.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionScreen(
    transactionId: Long,
    onBack: () -> Unit,
    viewModel: EditTransactionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(transactionId) {
        viewModel.loadTransaction(transactionId)
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onBack()
    }

    val selectedTypeIndex = when (state.type) {
        TransactionType.INCOME -> 0
        TransactionType.EXPENSE -> 1
        TransactionType.SAVING -> 2
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = { Text("Edit Transaksi", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        verticalArrangement = Arrangement.spacedBy(22.dp)
                    ) {
                        CpSegmentedButton(
                            options = listOf("Penghasilan", "Pengeluaran", "Tabungan"),
                            selectedIndex = selectedTypeIndex,
                            onSelected = { index ->
                                viewModel.setType(when (index) {
                                    0 -> TransactionType.INCOME
                                    1 -> TransactionType.EXPENSE
                                    else -> TransactionType.SAVING
                                })
                            }
                        )

                        CpRupiahTextField(
                            value = state.amount,
                            onValueChange = viewModel::setAmount,
                            label = "Nominal",
                            placeholder = "0"
                        )

                        if (state.type == TransactionType.SAVING) {
                            val selectedGoal = state.savingGoals.firstOrNull { it.id == state.savingGoalId }
                            CpDropdown(
                                label = "Target Tabungan",
                                options = state.savingGoals.map { it.name },
                                selectedOption = selectedGoal?.name,
                                enabled = !state.isLoadingSavingGoals && !state.isSaving && state.savingGoals.isNotEmpty(),
                                placeholder = when {
                                    state.isLoadingSavingGoals -> "Memuat target tabungan..."
                                    state.savingGoals.isEmpty() -> "Belum ada target tabungan"
                                    else -> "Pilih target tabungan"
                                },
                                onOptionSelected = { index ->
                                    state.savingGoals.getOrNull(index)?.let { viewModel.setSavingGoalId(it.id) }
                                }
                            )
                        } else {
                            val selectedCategory = state.categories.firstOrNull { it.id == state.categoryId }
                            CpDropdown(
                                label = "Kategori",
                                options = state.categories.map { it.name },
                                selectedOption = selectedCategory?.name,
                                enabled = !state.isLoadingCategories && !state.isSaving && state.categories.isNotEmpty(),
                                placeholder = when {
                                    state.isLoadingCategories -> "Memuat kategori..."
                                    state.categories.isEmpty() -> "Belum ada kategori"
                                    else -> "Pilih kategori"
                                },
                                onOptionSelected = { index ->
                                    state.categories.getOrNull(index)?.let { viewModel.setCategoryId(it.id) }
                                }
                            )
                        }

                        CpDatePicker(
                            date = state.transactionDate,
                            onDateSelected = viewModel::setDate
                        )

                        CpTextField(
                            value = state.note,
                            onValueChange = viewModel::setNote,
                            label = "Catatan",
                            placeholder = "Tambahkan catatan (opsional)",
                            singleLine = false,
                            minLines = 3,
                            maxLines = 5
                        )

                        state.error?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        CpButton(
                            text = "Simpan Perubahan",
                            onClick = { viewModel.save() },
                            enabled = !state.isSaving,
                            loading = state.isSaving
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
