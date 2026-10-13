package com.example.cashpeer.feature.savinggoal.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cashpeer.feature.savinggoal.domain.model.SavingFrequency
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
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = if (goalId == null) {
                    "Tambah Target Tabungan"
                } else {
                    "Edit Target Tabungan"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Rencanakan tujuanmu dan tentukan nominal yang ingin dicapai.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Memuat data target...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = viewModel::onNameChange,
                        label = { Text("Nama target") },
                        placeholder = { Text("Contoh: Beli laptop") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving
                    )

                    OutlinedTextField(
                        value = uiState.targetAmount,
                        onValueChange = viewModel::onTargetAmountChange,
                        label = { Text("Nominal target (Rp)") },
                        placeholder = { Text("Contoh: 5000000") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Frekuensi menabung",
                            style = MaterialTheme.typography.labelLarge
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SavingFrequency.entries.forEach { frequency ->
                                FilterChip(
                                    selected = uiState.savingFrequency == frequency,
                                    onClick = {
                                        viewModel.onSavingFrequencyChange(frequency)
                                    },
                                    label = { Text(frequency.label) },
                                    enabled = !uiState.isSaving
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = uiState.savingAmount,
                        onValueChange = viewModel::onSavingAmountChange,
                        label = { Text("Nominal tabungan rutin (Rp)") },
                        placeholder = { Text("Contoh: 50000") },
                        supportingText = {
                            Text(
                                "Nominal yang akan kamu sisihkan ${uiState.savingFrequency.label.lowercase()}."
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Tanggal target",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        val formattedDate = uiState.targetDate?.let {
                            runCatching {
                                LocalDate.parse(it).format(
                                    DateTimeFormatter.ofPattern(
                                        "dd MMMM yyyy",
                                        Locale("id", "ID")
                                    )
                                )
                            }.getOrDefault(it)
                        }

                        OutlinedTextField(
                            value = formattedDate ?: "",
                            onValueChange = {},
                            readOnly = true,
                            enabled = true,
                            placeholder = {
                                Text("Pilih tanggal (opsional)")
                            },
                            trailingIcon = {
                                TextButton(
                                    onClick = { showDatePicker = true },
                                    enabled = !uiState.isSaving
                                ) {
                                    Text("Pilih")
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !uiState.isSaving) {
                                    showDatePicker = true
                                },
                            singleLine = true
                        )

                        if (uiState.targetDate != null) {
                            TextButton(
                                onClick = viewModel::onClearTargetDate,
                                enabled = !uiState.isSaving
                            ) {
                                Text("Hapus tanggal")
                            }
                        }
                    }

                    OutlinedTextField(
                        value = uiState.note,
                        onValueChange = viewModel::onNoteChange,
                        label = { Text("Catatan (opsional)") },
                        placeholder = { Text("Tambahkan catatan") },
                        shape = RoundedCornerShape(14.dp),
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

                    Spacer(Modifier.height(2.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onBack,
                            enabled = !uiState.isSaving,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Batal")
                        }

                        Button(
                            onClick = viewModel::save,
                            enabled = !uiState.isSaving,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = if (uiState.isSaving) {
                                    "Menyimpan..."
                                } else {
                                    "Simpan"
                                },
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
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

        val datePickerState = rememberDatePickerState(
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