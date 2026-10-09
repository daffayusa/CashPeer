package com.example.cashpeer.feature.transaction.presentation.add

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cashpeer.core.ui.components.CpButton
import com.example.cashpeer.core.ui.components.CpDatePicker
import com.example.cashpeer.core.ui.components.CpDropdown
import com.example.cashpeer.core.ui.components.CpSegmentedButton
import com.example.cashpeer.core.ui.components.CpTextField
import com.example.cashpeer.core.ui.theme.CashPeerTheme
import com.example.cashpeer.feature.category.domain.usecase.GetCategoryUseCase
import com.example.cashpeer.feature.transaction.domain.model.TransactionType
import com.example.cashpeer.feature.transaction.domain.usecase.AddTransactionUseCase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val selectedTypeIndex = when (state.type) {
        TransactionType.INCOME -> 0
        TransactionType.EXPENSE -> 1
        TransactionType.SAVING -> 1
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transaksi",
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
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                //.verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(22.dp)
                ) {

                    // Tipe transaksi
                    CpSegmentedButton(
                        options = listOf(
                            "Penghasilan",
                            "Pengeluaran"
                        ),
                        selectedIndex = selectedTypeIndex,
                        onSelected = { index ->
                            viewModel.setType(
                                if (index == 0) {
                                    TransactionType.INCOME
                                } else {
                                    TransactionType.EXPENSE
                                }
                            )
                        }
                    )

                    // Nominal
                    CpTextField(
                        value = state.amount,
                        onValueChange = viewModel::setAmount,
                        label = "Nominal",
                        placeholder = "Masukkan nominal",
                        keyboardType = KeyboardType.Number
                    )

                    // Kategori
                    val selectedCategory = state.categories
                        .firstOrNull { it.id == state.categoryId }

                    CpDropdown(
                        label = "Kategori",
                        options = state.categories.map { it.name },
                        selectedOption = selectedCategory?.name,
                        enabled = !state.isLoadingCategories,
                        placeholder = when {
                            state.isLoadingCategories -> "Memuat kategori..."
                            state.categories.isEmpty() -> "Belum ada kategori"
                            else -> "Pilih kategori"
                        },
                        onOptionSelected = { index ->
                            state.categories.getOrNull(index)?.let {
                                viewModel.setCategoryId(it.id)
                            }
                        }
                    )

                    // Tanggal
                    CpDatePicker(
                        date = state.transactionDate,
                        onDateSelected = viewModel::setDate
                    )

                    // Catatan
                    CpTextField(
                        value = state.note,
                        onValueChange = viewModel::setNote,
                        label = "Catatan",
                        placeholder = "Tambahkan catatan (opsional)",
                        singleLine = false,
                        minLines = 3,
                        maxLines = 5
                    )

                    // Pesan error
                    state.error?.let { error ->
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(54.dp))

                    // Tombol simpan
                    CpButton(
                        text = "Simpan",
                        onClick = {
                            viewModel.save {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Transaksi berhasil disimpan"
                                    )
                                }
                                onSuccess()
                            }
                        },
                        enabled = !state.isSaving,
                        loading = state.isSaving
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Preview
@Composable
private fun AddTransactionPrev() {
    CashPeerTheme {
        AddTransactionScreen(
            {},
            {}
        )
    }
}