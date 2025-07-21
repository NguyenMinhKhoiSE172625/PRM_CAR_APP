package com.example.prmcar.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.prmcar.data.model.CarResponse
import com.example.prmcar.presentation.viewmodel.CarViewModel
import com.example.prmcar.presentation.util.formatPrice
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailScreen(
    carViewModel: CarViewModel,
    selectedCar: CarResponse?,
    carId: Int,
    onNavigateBack: () -> Unit,
    onEditClick: (Int) -> Unit
) {
    LaunchedEffect(key1 = carId) {
        carViewModel.getCarById(carId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedCar?.carName ?: "Car Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditClick(carId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Car")
                    }
                    IconButton(onClick = {
                        carViewModel.deleteCar(carId)
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Car")
                    }
                }
            )
        }
    ) { padding ->
        if (selectedCar == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DetailItem("Make", selectedCar.make)
                DetailItem("Model", selectedCar.model)
                DetailItem("Year", selectedCar.manufactureYear.toString())
                DetailItem("Car Type", selectedCar.carTypeName)
                DetailItem("Color", selectedCar.color)
                DetailItem("Mileage", selectedCar.mileage?.let { "${NumberFormat.getInstance().format(it)} km" })
                DetailItem("License Plate", selectedCar.licensePlate)
                DetailItem("Asking Price", formatPrice(selectedCar.askingPrice))
                DetailItem("Status", selectedCar.status)
                DetailItem("Description", selectedCar.description)
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
} 