package com.example.bunnix.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.bunnix.database.models.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartViewModel : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(item: CartItem) {
        val current = _cartItems.value.toMutableList()

        val existing = current.find { it.id == item.id }

        if (existing != null) {
            val updated = existing.copy(quantity = existing.quantity + item.quantity)
            current.remove(existing)
            current.add(updated)
        } else {
            current.add(item)
        }

        _cartItems.value = current
    }

    fun removeFromCart(id: String) {
        _cartItems.value = _cartItems.value.filter { it.id != id }
    }

    fun updateQuantity(id: String, quantity: Int) {
        _cartItems.value = _cartItems.value.map {
            if (it.id == id) it.copy(quantity = quantity) else it
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }
}