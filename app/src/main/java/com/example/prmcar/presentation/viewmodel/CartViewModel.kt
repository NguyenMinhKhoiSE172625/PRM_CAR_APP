package com.example.prmcar.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CartItemUi(
    val carId: Int,
    val carName: String,
    val price: Int,
    val quantity: Int = 1
)

class CartViewModel : ViewModel() {
    private val _cartItems = MutableStateFlow<List<CartItemUi>>(emptyList())
    val cartItems: StateFlow<List<CartItemUi>> = _cartItems.asStateFlow()

    private val _totalPrice = MutableStateFlow(0)
    val totalPrice: StateFlow<Int> = _totalPrice.asStateFlow()

    fun addToCart(item: CartItemUi) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.carId == item.carId }
        if (index >= 0) {
            val old = current[index]
            current[index] = old.copy(quantity = old.quantity + 1)
        } else {
            current.add(item)
        }
        _cartItems.value = current
        recalculateTotal()
    }

    fun removeFromCart(carId: Int) {
        val current = _cartItems.value.toMutableList()
        current.removeAll { it.carId == carId }
        _cartItems.value = current
        recalculateTotal()
    }

    private fun recalculateTotal() {
        val total = _cartItems.value.sumOf { it.price.toLong() * it.quantity }
        _totalPrice.value = if (total < 0) 0 else total.toInt()
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _totalPrice.value = 0
    }
} 