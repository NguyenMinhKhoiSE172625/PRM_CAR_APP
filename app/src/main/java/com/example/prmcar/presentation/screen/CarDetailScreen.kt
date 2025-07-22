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
    onEditClick: (Int) -> Unit,
    userType: String?,
    currentUserId: Int?
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
                    if (userType == "Admin") {
                        IconButton(onClick = { onEditClick(carId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Car")
                        }
                        IconButton(onClick = {
                            carViewModel.deleteCar(carId)
                            onNavigateBack()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Car")
                        }
                    } else if (userType == "Seller" && selectedCar?.sellerId == currentUserId) {
                        IconButton(onClick = { onEditClick(carId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Car")
                        }
                    }
                    // Buyer: không hiện gì
                }
            )
        }
    ) { padding ->
        if (selectedCar == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Card(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = selectedCar.carName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatPrice(selectedCar.askingPrice),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Divider()
                    DetailRow("Hãng", selectedCar.make)
                    DetailRow("Dòng xe", selectedCar.model)
                    DetailRow("Năm sản xuất", selectedCar.manufactureYear.toString())
                    DetailRow("Loại xe", selectedCar.carTypeName)
                    DetailRow("Màu sắc", selectedCar.color)
                    DetailRow("Số km đã đi", selectedCar.mileage?.let { "${NumberFormat.getInstance().format(it)} km" })
                    DetailRow("Biển số", selectedCar.licensePlate)
                    DetailRow("Trạng thái", selectedCar.status)
                    Divider()
                    Text(
                        text = "Mô tả:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = selectedCar.description ?: "Không có mô tả",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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