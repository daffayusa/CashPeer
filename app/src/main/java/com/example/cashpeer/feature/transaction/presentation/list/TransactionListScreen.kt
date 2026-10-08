package com.example.cashpeer.feature.transaction.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cashpeer.feature.transaction.domain.model.Transaction
import java.nio.file.WatchEvent


@Composable
fun TransactionListScreen(
    onAddTransaction: () -> Unit,
    viewModel: TransactionListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = onAddTransaction,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("TambahTransaksi")
        }
        TransactionListContent(
            uiState = uiState
        )
    }

}

@Composable
fun TransactionListContent(

    uiState: TransactionListUiState
) {
    when{
        uiState.isLoading -> {
            CircularProgressIndicator()
        }

        uiState.error != null ->{
            Text(
                text = uiState.error
            )
        }

        uiState.transactions.isEmpty() ->{
            EmptyTransactionState()
        }

        else ->{
            TransactionList(
                transactions = uiState.transactions
            )
        }
    }
}

@Composable
private fun TransactionList(
    transactions: List<Transaction>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = transactions,
            key = { transaction -> transaction.id }
        ) { transaction ->

            TransactionItem(
                transaction = transaction
            )
        }
    }
}




@Composable
private fun TransactionItem(
    transaction: Transaction
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = transaction.type.name
        )

        Text(
            text = "Rp ${transaction.amount}"
        )

        Text(
            text = transaction.transactionDate.toString()
        )

        transaction.note?.let { note ->
            Text(text = note)
        }
    }

}

@Composable
private fun EmptyTransactionState() {
    Text(
        text = "Belum ada transaksi"
    )
}