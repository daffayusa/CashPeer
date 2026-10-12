package com.example.cashpeer.core.ui.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cashpeer.feature.savinggoal.presentation.model.SavingGoalItemUi
import java.text.NumberFormat
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun SavingGoalItem(item:SavingGoalItemUi, modifier: Modifier = Modifier) {
    val currencyFormat = NumberFormat.getCurrencyInstance(
        Locale("id", "ID")
    ).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = item.goal.name,
                    style = MaterialTheme.typography.titleMedium
                )

                if (item.isCompleted){
                    Text(
                        text = "Tercapai",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Text(
                text = currencyFormat.format(item.savedAmount),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            LinearProgressIndicator(
                progress = {item.progress},
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(item.progress*100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Target ${currencyFormat.format(item.goal.targetAmount)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = if(item.isCompleted){
                    "Target tabungan sudah tercapai"
                }else{
                    "Kurang ${currencyFormat.format(item.remainingAmount)} lagi"
                },
                style = MaterialTheme.typography.bodyMedium
            )
            item.targetDate?.let { date ->
                Text(
                    text = "Targer tanggal ${
                        date.format((DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id", "ID"))))
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item.goal.note?.takeIf { it.isNotBlank() }?.let { note ->
                Text(
                    text = note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}