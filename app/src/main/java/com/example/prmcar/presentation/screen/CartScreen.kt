@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.prmcar.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prmcar.presentation.viewmodel.CartViewModel
import com.example.prmcar.presentation.viewmodel.CartItemUi
import java.text.NumberFormat
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.prmcar.presentation.viewmodel.TransactionViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CartScreen(
    cartViewModel: CartViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel(),
    currentUserId: Int?,
    onCheckout: (List<CartItemUi>, Int) -> Unit = { _, _ -> },
    onBack: (() -> Unit)? = null,
    onNavigateHome: (() -> Unit)? = null
) {
    var showInvoice by remember { mutableStateOf(false) }
    var isProcessingPayment by remember { mutableStateOf(false) }

    // Lưu data cho invoice trước khi clear cart
    var invoiceCartItems by remember { mutableStateOf<List<CartItemUi>>(emptyList()) }
    var invoiceTotalPrice by remember { mutableStateOf(0) }
    val cartItems by cartViewModel.cartItems.collectAsState()
    val transactionUiState by transactionViewModel.uiState.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()
    val numberFormat = remember { NumberFormat.getInstance(Locale("vi", "VN")) }
    val safeTotal = if (totalPrice < 0) 0 else totalPrice

    // Xử lý kết quả transaction
    LaunchedEffect(transactionUiState.isActionSuccess) {
        if (transactionUiState.isActionSuccess && isProcessingPayment) {
            isProcessingPayment = false

            // Lưu data cho invoice TRƯỚC khi clear cart
            invoiceCartItems = cartItems.toList()
            invoiceTotalPrice = safeTotal
            println("DEBUG: Saved invoice data - items=${invoiceCartItems.size}, total=$invoiceTotalPrice")

            cartViewModel.clearCart() // Xóa cart sau khi lưu data
            showInvoice = true
            transactionViewModel.clearActionSuccess()
        }
    }

    // Xử lý lỗi transaction
    LaunchedEffect(transactionUiState.errorMessage) {
        if (transactionUiState.errorMessage != null && isProcessingPayment) {
            isProcessingPayment = false
            println("DEBUG: Transaction error: ${transactionUiState.errorMessage}")
            // TODO: Hiển thị error dialog cho user
        }
    }


    // Debug button enable conditions
    val buttonEnabled = !isProcessingPayment && cartItems.isNotEmpty() && currentUserId != null
    println("DEBUG: === CART SCREEN DEBUG ===")
    println("DEBUG: Button enabled = $buttonEnabled")
    println("DEBUG: isProcessingPayment = $isProcessingPayment")
    println("DEBUG: cartItems.isNotEmpty() = ${cartItems.isNotEmpty()}")
    println("DEBUG: cartItems.size = ${cartItems.size}")
    println("DEBUG: currentUserId != null = ${currentUserId != null}")
    println("DEBUG: currentUserId = $currentUserId")
    println("DEBUG: =========================")

    if (showInvoice) {
        InvoiceScreen(
            cartItems = invoiceCartItems, // Dùng saved data
            totalPrice = invoiceTotalPrice, // Dùng saved data
            onBack = {
                showInvoice = false
                // Cart đã được clear rồi, không cần clear lại
                onNavigateHome?.invoke()
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Giỏ hàng của bạn") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateHome?.invoke() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Về trang chủ")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(MaterialTheme.colorScheme.background, Color.White)
                    )
                )
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)) {
                if (cartItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Giỏ hàng trống", style = MaterialTheme.typography.titleMedium)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(cartItems, key = { it.carId }) { item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(18.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(item.carName, style = MaterialTheme.typography.titleMedium)
                                        Text("Số lượng: ${item.quantity}", style = MaterialTheme.typography.bodyMedium)
                                    }
                                    Text(
                                        text = numberFormat.format(item.price) + " đ",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    IconButton(
                                        onClick = { cartViewModel.removeFromCart(item.carId) },
                                        modifier = Modifier.background(
                                            color = MaterialTheme.colorScheme.errorContainer,
                                            shape = RoundedCornerShape(50)
                                        )
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Xóa vật phẩm", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Tổng tiền: ${numberFormat.format(safeTotal)} VNĐ",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            println("DEBUG: Button clicked!")
                            println("DEBUG: currentUserId = $currentUserId")
                            println("DEBUG: cartItems.size = ${cartItems.size}")
                            println("DEBUG: isProcessingPayment = $isProcessingPayment")

                            if (currentUserId != null && cartItems.isNotEmpty()) {
                                println("DEBUG: Starting payment process...")
                                isProcessingPayment = true
                                // Tạo transaction cho 1 car (item đầu tiên trong cart)
                                val firstItem = cartItems.first()
                                println("DEBUG: Creating transaction for carId=${firstItem.carId}, buyerId=$currentUserId, price=${firstItem.price}")
                                transactionViewModel.purchaseCar(
                                    carId = firstItem.carId,
                                    buyerId = currentUserId,
                                    price = firstItem.price.toDouble()
                                )
                            } else {
                                println("DEBUG: Payment conditions not met")
                                if (currentUserId == null) println("DEBUG: currentUserId is null")
                                if (cartItems.isEmpty()) println("DEBUG: cartItems is empty")

                                // Không hiển thị invoice nếu conditions không đúng
                                // showInvoice = true // Removed fallback
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        enabled = !isProcessingPayment && cartItems.isNotEmpty() && currentUserId != null
                    ) {
                        if (isProcessingPayment) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Thanh toán", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InvoiceScreen(
    cartItems: List<CartItemUi>,
    totalPrice: Int,
    onBack: () -> Unit
) {
    val numberFormat = remember { NumberFormat.getInstance(Locale("vi", "VN")) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hóa đơn thanh toán") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Về trang chủ")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(MaterialTheme.colorScheme.background, Color.White)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Text("Cảm ơn bạn đã mua hàng!", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Chi tiết hóa đơn:", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        cartItems.forEach {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${it.carName} x${it.quantity}", style = MaterialTheme.typography.bodyLarge)
                                Text(numberFormat.format(it.price * it.quantity) + " đ", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Tổng cộng: ${numberFormat.format(totalPrice)} VNĐ", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Về trang chủ", color = Color.White)
                }
            }
        }
    }
} 