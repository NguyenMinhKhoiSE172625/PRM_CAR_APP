package com.example.prmcar.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.prmcar.data.model.CarResponse
import com.example.prmcar.data.utils.rememberImageUploadLauncher
import com.example.prmcar.presentation.viewmodel.AuthUiState
import com.example.prmcar.presentation.viewmodel.CarUiState
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailScreen(
    carUiState: CarUiState,
    authUiState: AuthUiState,
    onBackClick: () -> Unit,
    onDeleteClick: (Int) -> Unit,
    onUploadImage: (Int) -> Unit
) {
    val selectedCar = carUiState.selectedCar
    val context = LocalContext.current
    
    // Image upload launcher
    val imageUploadLauncher = rememberImageUploadLauncher { uri ->
        selectedCar?.let { car ->
            onUploadImage(car.carId)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar - Không có nút edit
        TopAppBar(
            title = { Text("Car Details") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                selectedCar?.let { car ->
                    IconButton(onClick = { /* TODO: Implement edit logic */ }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            }
        )

        // Content
        if (carUiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (selectedCar != null) {
            CarDetailContent(
                car = selectedCar,
                authUiState = authUiState,
                onDeleteClick = { onDeleteClick(selectedCar.carId) },
                onUploadImage = { imageUploadLauncher.launch("image/*") }
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Car not found")
            }
        }
        
        // Loading indicator for image upload
        if (carUiState.isUploadingImage) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Uploading image...")
                    }
                }
            }
        }
    }
}

@Composable
private fun CarDetailContent(
    car: CarResponse,
    authUiState: AuthUiState,
    onDeleteClick: () -> Unit,
    onUploadImage: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Car Image Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if (!car.imageUrl.isNullOrBlank()) {
                    // Hiển thị ảnh từ URL
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(car.imageUrl ?: "")
                            .crossfade(true)
                            .build(),
                        contentDescription = "Car Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Placeholder khi chưa có ảnh
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
                                text = "No Image Available",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Upload button overlay
                FloatingActionButton(
                    onClick = onUploadImage,
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

        // Car Information
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = car.carName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${car.make} ${car.model}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Price:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = formatPrice(car.askingPrice),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Year
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Year:")
                    Text(car.manufactureYear.toString())
                }
                
                // Mileage
                car.mileage?.let { mileage ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mileage:")
                        Text("${NumberFormat.getInstance().format(mileage)} km")
                    }
                }
                
                // Color
                car.color?.let { color ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Color:")
                        Text(color)
                    }
                }
                
                // License Plate
                car.licensePlate?.let { plate ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("License Plate:")
                        Text(plate)
                    }
                }
                
                // Car Type
                car.carTypeName?.let { type ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Type:")
                        Text(type)
                    }
                }
                
                // Status
                car.status?.let { status ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Status:")
                        Text(status)
                    }
                }
                
                // Description
                car.description?.let { description ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Description:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Delete Button (chỉ hiển thị cho Admin và Seller)
        if (authUiState.userType == "Admin" || authUiState.userType == "Seller") {
            Button(
                onClick = onDeleteClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete Car")
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return formatter.format(price)
} 