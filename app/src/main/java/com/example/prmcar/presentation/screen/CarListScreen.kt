package com.example.prmcar.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.prmcar.data.model.CarResponse
import com.example.prmcar.presentation.viewmodel.AuthUiState
import com.example.prmcar.presentation.viewmodel.CarUiState
import com.example.prmcar.presentation.util.formatPrice
import java.text.NumberFormat
import java.util.*
import com.example.prmcar.presentation.viewmodel.CartViewModel
import com.example.prmcar.presentation.viewmodel.CartItemUi
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarHost
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarListScreen(
    carUiState: CarUiState,
    authUiState: AuthUiState,
    onCarClick: (Int) -> Unit,
    onAddCarClick: () -> Unit,
    onRefresh: () -> Unit,
    onLogout: () -> Unit,
    onSearch: (String) -> Unit,
    onCarTypeManageClick: () -> Unit,
    onTransactionManageClick: () -> Unit,
    onUserManageClick: () -> Unit,
    cartViewModel: CartViewModel = viewModel(),
    onCartClick: () -> Unit // callback sang CartScreen
) {
    var searchQuery by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    Column {
                        Text("HỆ THỐNG QUẢN LÍ XE")
                        authUiState.userEmail?.let { email ->
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onCarTypeManageClick) {
                        Icon(Icons.Default.ListAlt, contentDescription = "Manage Car Types")
                    }
                    IconButton(onClick = onTransactionManageClick) {
                        Icon(Icons.Default.Receipt, contentDescription = "Manage Transactions")
                    }
                    IconButton(onClick = onUserManageClick) {
                        Icon(Icons.Default.Person, contentDescription = "Manage Users")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    onSearch(it)
                },
                label = { Text("Search cars...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )
            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    carUiState.isLoading && carUiState.cars.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    carUiState.cars.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No cars found",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Pull to refresh or add a new car",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(carUiState.cars) { car ->
                                CarItem(
                                    car = car,
                                    onClick = { onCarClick(car.carId) },
                                    userType = authUiState.userType,
                                    currentUserId = authUiState.userId,
                                    onAddToCart = { carResp ->
                                        if (authUiState.userType == "Buyer") {
                                            cartViewModel.addToCart(
                                                CartItemUi(
                                                    carId = carResp.carId,
                                                    carName = carResp.carName,
                                                    price = carResp.askingPrice.toInt()
                                                )
                                            )
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Đã thêm vào giỏ hàng!")
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                // Error Message
                carUiState.errorMessage?.let { error ->
                    val errorSnackbarHostState = remember { SnackbarHostState() }
                    LaunchedEffect(error) {
                        errorSnackbarHostState.showSnackbar(
                            message = error,
                            actionLabel = "Retry",
                            duration = SnackbarDuration.Long
                        )
                    }
                    SnackbarHost(
                        hostState = errorSnackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
                // Floating Action Button
                if (authUiState.userType == "Buyer") {
                    val cartCount by cartViewModel.cartItems.collectAsState()
                    FloatingActionButton(
                        onClick = onCartClick,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        BadgedBox(
                            badge = {
                                if (cartCount.isNotEmpty()) {
                                    Badge { Text(cartCount.sumOf { it.quantity }.toString()) }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Giỏ hàng")
                        }
                    }
                } else if (authUiState.userType == "Admin" || authUiState.userType == "Seller") {
                    FloatingActionButton(
                        onClick = onAddCarClick,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Car")
                    }
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarItem(
    car: CarResponse,
    onClick: () -> Unit,
    userType: String?,
    currentUserId: Int?,
    onEdit: ((Int) -> Unit)? = null,
    onDelete: ((Int) -> Unit)? = null,
    onAddToCart: ((CarResponse) -> Unit)? = null // Thêm callback
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Car Name and Make
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = car.carName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${car.make} ${car.model}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = car.manufactureYear.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                // Nút Sửa/Xóa
                if (userType == "Admin") {
                    IconButton(onClick = { onEdit?.invoke(car.carId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Car")
                    }
                    IconButton(onClick = { onDelete?.invoke(car.carId) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Car")
                    }
                } else if (userType == "Seller" && car.sellerId == currentUserId) {
                    IconButton(onClick = { onEdit?.invoke(car.carId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Car")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price and Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatPrice(car.askingPrice),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                car.mileage?.let { mileage ->
                    Text(
                        text = "${NumberFormat.getInstance().format(mileage)} km",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Additional Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                car.color?.let { color ->
                    Text(
                        text = color,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                car.carTypeName?.let { type ->
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = type,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
            // Thêm nút Thêm vào giỏ cho Buyer ở dưới cùng
            if (userType == "Buyer") {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { onAddToCart?.invoke(car) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Thêm vào giỏ hàng")
                }
            }
        }
    }
} 