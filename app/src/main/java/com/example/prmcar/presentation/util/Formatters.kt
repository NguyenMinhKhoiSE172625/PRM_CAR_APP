package com.example.prmcar.presentation.util

import java.text.NumberFormat
import java.util.*
 
fun formatPrice(price: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return formatter.format(price)
} 