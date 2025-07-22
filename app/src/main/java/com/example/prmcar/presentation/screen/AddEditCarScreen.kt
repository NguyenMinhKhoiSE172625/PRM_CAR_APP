package com.example.prmcar.presentation.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.prmcar.data.model.CarRequest
import com.example.prmcar.presentation.viewmodel.CarUiState
import com.example.prmcar.presentation.viewmodel.CarViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCarScreen(
    carViewModel: CarViewModel,
    carUiState: CarUiState,
    carId: Int?,
    onNavigateBack: () -> Unit
) {
    val isEditMode = carId != null
    var carName by remember { mutableStateOf("") }
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var manufactureYear by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }
    var askingPrice by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var listingDate by remember { mutableStateOf("") }
    var sellerId by remember { mutableStateOf("") }
    
    var selectedCarTypeId by remember { mutableStateOf<Int?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    // Fetch car details if in edit mode
    LaunchedEffect(key1 = carId) {
        if (isEditMode) {
            carViewModel.getCarById(carId!!)
        } else {
            carViewModel.clearSelectedCar()
        }
    }

    // Populate fields when car details and carTypes are loaded for editing
    LaunchedEffect(carUiState.selectedCar, carUiState.carTypes) {
        if (isEditMode && carUiState.selectedCar != null && carUiState.carTypes.isNotEmpty()) {
            val car = carUiState.selectedCar
            carName = car.carName
            make = car.make
            model = car.model
            manufactureYear = car.manufactureYear.toString()
            color = car.color ?: ""
            mileage = car.mileage?.toString() ?: ""
            licensePlate = car.licensePlate ?: ""
            askingPrice = car.askingPrice.toString()
            description = car.description ?: ""
            selectedCarTypeId = car.carTypeId
            listingDate = car.listingDate ?: ""
            sellerId = car.sellerId?.toString() ?: ""
        }
    }

    // Lắng nghe sự kiện thành công
    LaunchedEffect(carViewModel) {
        carViewModel.carActionSuccess.collectLatest {
            onNavigateBack()
        }
    }

    // Hiển thị lỗi nếu có
    carUiState.errorMessage?.let { error ->
        Box(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { carViewModel.clearError() }) {
                        Text("Đóng")
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Car" else "Add New Car") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                Log.d("CAR_DEBUG", "Update/Create Car: id=$carId, request=" +
                    "carName=$carName, make=$make, model=$model, year=$manufactureYear, carTypeId=$selectedCarTypeId, color=$color, mileage=$mileage, licensePlate=$licensePlate, askingPrice=$askingPrice, description=$description, listingDate=$listingDate, sellerId=$sellerId")
                val carRequest = CarRequest(
                    carName = carName,
                    make = make,
                    model = model,
                    manufactureYear = manufactureYear.toIntOrNull() ?: 0,
                    carTypeId = selectedCarTypeId,
                    color = color,
                    mileage = mileage.toIntOrNull(),
                    licensePlate = licensePlate,
                    askingPrice = askingPrice.toDoubleOrNull() ?: 0.0,
                    description = description,
                    status = "Available",
                    listingDate = if (listingDate.isNotBlank()) listingDate else null,
                    sellerId = sellerId.toIntOrNull()
                )
                if (isEditMode) {
                    carViewModel.updateCar(carId!!, carRequest)
                } else {
                    carViewModel.createCar(carRequest)
                }
                // onNavigateBack() sẽ được gọi khi thành công
            }) {
                Icon(Icons.Default.Done, contentDescription = "Save Car")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(value = carName, onValueChange = { carName = it }, label = { Text("Car Name") })
            OutlinedTextField(value = make, onValueChange = { make = it }, label = { Text("Make (e.g., Toyota)") })
            OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("Model (e.g., Camry)") })
            OutlinedTextField(
                value = manufactureYear,
                onValueChange = { manufactureYear = it },
                label = { Text("Year") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Car Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = carUiState.carTypes.find { it.carTypeId == selectedCarTypeId }?.typeName ?: "",
                    onValueChange = { },
                    label = { Text("Car Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
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
            
            OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") })
            OutlinedTextField(
                value = mileage,
                onValueChange = { mileage = it },
                label = { Text("Mileage (km)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(value = licensePlate, onValueChange = { licensePlate = it }, label = { Text("License Plate") })
            OutlinedTextField(
                value = askingPrice,
                onValueChange = { askingPrice = it },
                label = { Text("Asking Price (VND)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                maxLines = 4
            )
            OutlinedTextField(
                value = listingDate,
                onValueChange = { listingDate = it },
                label = { Text("Listing Date (yyyy-MM-dd)") }
            )
            OutlinedTextField(
                value = sellerId,
                onValueChange = { sellerId = it },
                label = { Text("Seller ID") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
} 