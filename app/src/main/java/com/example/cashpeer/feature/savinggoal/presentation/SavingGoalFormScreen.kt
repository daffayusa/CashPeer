package com.example.cashpeer.feature.savinggoal.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingGoalFormScreen(
    onBack: () -> Unit,
    goalId: Long? = null,
    modifier: Modifier = Modifier,
    viewModel: SavingGoalFormViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(goalId) {
        if (goalId != null) {
            viewModel.loadGoal(goalId)
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onBack()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (goalId == null) {
                "Tambah Target Tabungan"
            } else {
                "Edit Target Tabungan"
            },
            style = MaterialTheme.typography.headlineSmall
        )

        if (uiState.isLoading) {
            Text("Memuat data target...")
        } else {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nama target") },
                placeholder = { Text("Contoh: Beli laptop") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            )

            OutlinedTextField(
                value = uiState.targetAmount,
                onValueChange = viewModel::onTargetAmountChange,
                label = { Text("Nominal target (Rp)") },
                placeholder = { Text("Contoh: 5000000") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            )

            OutlinedTextField(
                value = uiState.targetDate?.let {
                    runCatching {
                        LocalDate.parse(it).format(
                            DateTimeFormatter.ofPattern(
                                "dd MMMM yyyy",
                                Locale("id", "ID")
                            )
                        )
                    }.getOrDefault(it)
                } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Tanggal target (opsional)") },
                placeholder = { Text("Pilih tanggal target") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !uiState.isSaving) {
                        showDatePicker = true
                    },
                enabled = false,
                trailingIcon = {
                    TextButton(
                        onClick = { showDatePicker = true },
                        enabled = !uiState.isSaving
                    ) {
                        Text("Pilih")
                    }
                }
            )

            if (uiState.targetDate != null) {
                TextButton(
                    onClick = viewModel::onClearTargetDate,
                    enabled = !uiState.isSaving
                ) {
                    Text("Hapus tanggal")
                }
            }

            OutlinedTextField(
                value = uiState.note,
                onValueChange = viewModel::onNoteChange,
                label = { Text("Catatan (opsional)") },
                placeholder = { Text("Tambahkan catatan") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                enabled = !uiState.isSaving
            )

            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onBack,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Batal")
                }

                Button(
                    onClick = viewModel::save,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (uiState.isSaving) "Menyimpan..." else "Simpan")
                }
            }
        }
    }

    if (showDatePicker) {
        val initialMillis = uiState.targetDate
            ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            ?.atStartOfDay(ZoneId.of("UTC"))
            ?.toInstant()
            ?.toEpochMilli()

        val datePickerState = androidx.compose.material3.rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()

                            viewModel.onTargetDateChange(date.toString())
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Pilih")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}