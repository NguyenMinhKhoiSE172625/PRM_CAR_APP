package com.example.prmcar.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.prmcar.data.model.TransactionRequest
import com.example.prmcar.presentation.viewmodel.TransactionUiState
import com.example.prmcar.presentation.viewmodel.TransactionViewModel
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    transactionUiState: TransactionUiState,
    transactionViewModel: TransactionViewModel,
    onEditTransaction: (Int) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Pair<Boolean, Int?>>(false to null) }
    var showDetailDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transactions Management") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (transactionUiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactionUiState.transactions) { transaction ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            transactionViewModel.getTransactionById(transaction.transactionId)
                                            showDetailDialog = true
                                        }
                                ) {
                                    Text(
                                        text = "Transaction #${transaction.transactionId}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Car ID: ${transaction.carId}, Buyer ID: ${transaction.buyerId}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Status: ${transaction.transactionStatus ?: ""}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                IconButton(onClick = { showEditDialog = true to transaction.transactionId }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = { transactionViewModel.deleteTransaction(transaction.transactionId) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail Dialog
    if (showDetailDialog && transactionUiState.selectedTransaction != null) {
        TransactionDetailDialog(
            transaction = transactionUiState.selectedTransaction,
            onDismiss = {
                showDetailDialog = false
                transactionViewModel.clearSelectedTransaction()
            }
        )
    }
    // Add Dialog
    if (showAddDialog) {
        TransactionDialog(
            title = "Add Transaction",
            onConfirm = { carId, buyerId, date, price, status ->
                transactionViewModel.createTransaction(
                    TransactionRequest(carId, buyerId, date, price, status)
                )
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
    // Edit Dialog
    if (showEditDialog.first && showEditDialog.second != null) {
        val editId = showEditDialog.second!!
        val editTransaction = transactionUiState.transactions.find { it.transactionId == editId }
        TransactionDialog(
            title = "Edit Transaction",
            initialCarId = editTransaction?.carId?.toString() ?: "",
            initialBuyerId = editTransaction?.buyerId?.toString() ?: "",
            initialDate = editTransaction?.transactionDate ?: "",
            initialPrice = editTransaction?.sellingPrice?.toString() ?: "",
            initialStatus = editTransaction?.transactionStatus ?: "",
            onConfirm = { carId, buyerId, date, price, status ->
                transactionViewModel.updateTransaction(
                    editId,
                    TransactionRequest(carId, buyerId, date, price, status)
                )
                showEditDialog = false to null
            },
            onDismiss = { showEditDialog = false to null }
        )
    }
}

@Composable
fun TransactionDialog(
    title: String,
    initialCarId: String = "",
    initialBuyerId: String = "",
    initialDate: String = "",
    initialPrice: String = "",
    initialStatus: String = "",
    onConfirm: (Int, Int, String?, Double, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var carId by remember { mutableStateOf(initialCarId) }
    var buyerId by remember { mutableStateOf(initialBuyerId) }
    var date by remember { mutableStateOf(initialDate) }
    var price by remember { mutableStateOf(initialPrice) }
    var status by remember { mutableStateOf(initialStatus) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = carId,
                    onValueChange = { carId = it },
                    label = { Text("Car ID") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = buyerId,
                    onValueChange = { buyerId = it },
                    label = { Text("Buyer ID") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Transaction Date (yyyy-MM-ddTHH:mm:ss)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Selling Price") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Status") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (carId.isNotBlank() && buyerId.isNotBlank() && price.isNotBlank())
                        onConfirm(
                            carId.toIntOrNull() ?: 0,
                            buyerId.toIntOrNull() ?: 0,
                            date.ifBlank { null },
                            price.toDoubleOrNull() ?: 0.0,
                            status.ifBlank { null }
                        )
                },
                enabled = carId.isNotBlank() && buyerId.isNotBlank() && price.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TransactionDetailDialog(transaction: com.example.prmcar.data.model.TransactionResponse, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Transaction Details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("ID: ${transaction.transactionId}")
                Text("Car ID: ${transaction.carId}")
                Text("Buyer ID: ${transaction.buyerId}")
                Text("Date: ${transaction.transactionDate ?: ""}")
                Text("Price: ${transaction.sellingPrice}")
                Text("Status: ${transaction.transactionStatus ?: ""}")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
} 