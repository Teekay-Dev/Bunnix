package com.example.bunnix.data

import androidx.compose.runtime.mutableStateListOf
import com.example.bunnix.database.models.CartItem

/**
 * CartData - UI CART STATE
 * Stores CartItem objects used by CartScreen
 */
object CartData {

    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    fun addToCart(item: CartItem) {

        val existing = _cartItems.find {
            it.id == item.id && it.variant == item.variant
        }

        if (existing != null) {

            val index = _cartItems.indexOf(existing)

            _cartItems[index] = existing.copy(
                quantity = existing.quantity + item.quantity
            )

        } else {
            _cartItems.add(item)
        }
    }

    fun removeFromCart(id: String) {
        _cartItems.removeAll { it.id == id }
    }

    fun updateQuantity(id: String, quantity: Int) {

        val index = _cartItems.indexOfFirst { it.id == id }

        if (index != -1) {
            _cartItems[index] = _cartItems[index].copy(
                quantity = quantity
            )
        }
    }

    fun clearCart() {
        _cartItems.clear()
    }

    fun getTotalPrice(): Double {
        return _cartItems.sumOf { it.price * it.quantity }
    }

    fun getItemCount(): Int {
        return _cartItems.sumOf { it.quantity }
    }

}