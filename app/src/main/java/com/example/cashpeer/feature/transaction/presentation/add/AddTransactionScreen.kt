package com.example.cashpeer.feature.transaction.presentation.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AddTransactionScreen(
    onSuccess: () -> Unit,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Tambah Transaksi")

        OutlinedTextField(
            value = uiState.amount,
            onValueChange = viewModel::setAmount,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Nominal")
            }
        )

        OutlinedTextField(
            value = uiState.note,
            onValueChange = viewModel::setNote,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Catatan")
            }
        )

        uiState.error?.let {
            Text(text = it)
        }

        Button(
            onClick = {
                viewModel.save(onSuccess)
            },
            enabled = !uiState.isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (uiState.isSaving) {
                    "Menyimpan..."
                } else {
                    "Simpan"
                }
            )
        }
    }
}