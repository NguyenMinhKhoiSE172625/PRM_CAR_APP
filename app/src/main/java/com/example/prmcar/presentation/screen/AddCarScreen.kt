package com.example.prmcar.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.prmcar.data.model.CarRequest
import com.example.prmcar.data.model.CarTypeResponse
import com.example.prmcar.data.utils.rememberImageUploadLauncher
import com.example.prmcar.presentation.viewmodel.AuthUiState
import com.example.prmcar.presentation.viewmodel.CarUiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCarScreen(
    carUiState: CarUiState,
    authUiState: AuthUiState,
    onBackClick: () -> Unit,
    onSaveClick: (CarRequest) -> Unit,
    onUploadImage: () -> Unit
) {
    var carName by remember { mutableStateOf("") }
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var manufactureYear by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }
    var askingPrice by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCarTypeId by remember { mutableStateOf<Int?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    
    // Image upload launcher
    val imageUploadLauncher = rememberImageUploadLauncher { uri ->
        // TODO: Implement image upload logic
        // For now, just set a placeholder URL
        imageUrl = "https://example.com/placeholder.jpg"
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Add New Car") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        val carRequest = CarRequest(
                            carName = carName,
                            make = make,
                            model = model,
                            manufactureYear = manufactureYear.toIntOrNull() ?: 0,
                            carTypeId = selectedCarTypeId,
                            color = color.ifBlank { null },
                            mileage = mileage.toIntOrNull(),
                            licensePlate = licensePlate.ifBlank { null },
                            askingPrice = askingPrice.toDoubleOrNull() ?: 0.0,
                            description = description.ifBlank { null },
                            status = "Available",
                            listingDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                            sellerId = null,
                            imageUrl = imageUrl
                        )
                        onSaveClick(carRequest)
                    },
                    enabled = carName.isNotBlank() && make.isNotBlank() && model.isNotBlank() && 
                             manufactureYear.isNotBlank() && askingPrice.isNotBlank()
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                }
            }
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Image Upload Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (!imageUrl.isNullOrBlank()) {
                        // Hiển thị ảnh đã upload
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Car Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Placeholder cho upload ảnh
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Camera,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Tap to upload car image",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    // Upload button
                    FloatingActionButton(
                        onClick = { imageUploadLauncher.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Upload Image")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Car Name
            OutlinedTextField(
                value = carName,
                onValueChange = { carName = it },
                label = { Text("Car Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Make and Model
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = make,
                    onValueChange = { make = it },
                    label = { Text("Make *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Year and Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = manufactureYear,
                    onValueChange = { manufactureYear = it },
                    label = { Text("Year *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = askingPrice,
                    onValueChange = { askingPrice = it },
                    label = { Text("Price *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Car Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = carUiState.carTypes.find { it.carTypeId == selectedCarTypeId }?.typeName ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Car Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    carUiState.carTypes.forEach { carType ->
                        DropdownMenuItem(
                            text = { Text(carType.typeName) },
                            onClick = {
                                selectedCarTypeId = carType.carTypeId
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Color and Mileage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Color") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = mileage,
                    onValueChange = { mileage = it },
                    label = { Text("Mileage (km)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // License Plate
            OutlinedTextField(
                value = licensePlate,
                onValueChange = { licensePlate = it },
                label = { Text("License Plate") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    val carRequest = CarRequest(
                        carName = carName,
                        make = make,
                        model = model,
                        manufactureYear = manufactureYear.toIntOrNull() ?: 0,
                        carTypeId = selectedCarTypeId,
                        color = color.ifBlank { null },
                        mileage = mileage.toIntOrNull(),
                        licensePlate = licensePlate.ifBlank { null },
                        askingPrice = askingPrice.toDoubleOrNull() ?: 0.0,
                        description = description.ifBlank { null },
                        status = "Available",
                        listingDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                        sellerId = null,
                        imageUrl = imageUrl
                    )
                    onSaveClick(carRequest)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = carName.isNotBlank() && make.isNotBlank() && model.isNotBlank() && 
                         manufactureYear.isNotBlank() && askingPrice.isNotBlank()
            ) {
                Text("Save Car")
            }
        }
    }
} 