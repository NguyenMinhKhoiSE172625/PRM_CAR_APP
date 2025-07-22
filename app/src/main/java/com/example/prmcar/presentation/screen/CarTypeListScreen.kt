package com.example.prmcar.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.prmcar.data.model.CarTypeRequest
import com.example.prmcar.presentation.viewmodel.CarTypeUiState
import com.example.prmcar.presentation.viewmodel.CarTypeViewModel
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarTypeListScreen(
    carTypeUiState: CarTypeUiState,
    carTypeViewModel: CarTypeViewModel,
    onEditCarType: (Int) -> Unit,
    onBack: () -> Unit,
    userType: String?
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Pair<Boolean, Int?>>(false to null) }
    var showDetailDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Car Types Management") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (userType == "Admin") {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Car Type")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (carTypeUiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(carTypeUiState.carTypes) { carType ->
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
                                            carTypeViewModel.getCarTypeById(carType.carTypeId)
                                            showDetailDialog = true
                                        }
                                ) {
                                    Text(
                                        text = carType.typeName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = carType.description ?: "",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                if (userType == "Admin") {
                                    IconButton(onClick = { showEditDialog = true to carType.carTypeId }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                    IconButton(onClick = { carTypeViewModel.deleteCarType(carType.carTypeId) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail Dialog
    if (showDetailDialog && carTypeUiState.selectedCarType != null) {
        CarTypeDetailDialog(
            carType = carTypeUiState.selectedCarType,
            onDismiss = {
                showDetailDialog = false
                carTypeViewModel.clearSelectedCarType()
            }
        )
    }
    // Add Dialog
    if (showAddDialog) {
        CarTypeDialog(
            title = "Add Car Type",
            onConfirm = { typeName, description ->
                carTypeViewModel.createCarType(CarTypeRequest(typeName, description))
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }
    // Edit Dialog
    if (showEditDialog.first && showEditDialog.second != null) {
        val editId = showEditDialog.second!!
        val editCarType = carTypeUiState.carTypes.find { it.carTypeId == editId }
        CarTypeDialog(
            title = "Edit Car Type",
            initialTypeName = editCarType?.typeName ?: "",
            initialDescription = editCarType?.description ?: "",
            onConfirm = { typeName, description ->
                carTypeViewModel.updateCarType(editId, CarTypeRequest(typeName, description))
                showEditDialog = false to null
            },
            onDismiss = { showEditDialog = false to null }
        )
    }
}

@Composable
fun CarTypeDialog(
    title: String,
    initialTypeName: String = "",
    initialDescription: String = "",
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var typeName by remember { mutableStateOf(initialTypeName) }
    var description by remember { mutableStateOf(initialDescription) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = typeName,
                    onValueChange = { typeName = it },
                    label = { Text("Type Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (typeName.isNotBlank()) onConfirm(typeName, description)
                },
                enabled = typeName.isNotBlank()
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
fun CarTypeDetailDialog(carType: com.example.prmcar.data.model.CarTypeResponse, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Car Type Details") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("ID: ${carType.carTypeId}")
                Text("Type Name: ${carType.typeName}")
                Text("Description: ${carType.description ?: ""}")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
} 