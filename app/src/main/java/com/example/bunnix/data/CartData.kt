package com.example.bunnix.data


import androidx.compose.runtime.mutableStateListOf
import com.example.bunnix.database.models.Product  // ✅ BACKEND MODEL

/**
 * CartData - BACKEND INTEGRATED
 * Manages cart items using backend Product model
 */
object CartData {

    // ✅ BACKEND: Use backend Product type
    private val _cartItems = mutableStateListOf<Product>()
    val cartItems: List<Product> get() = _cartItems

    /**
     * Add product to cart
     */
    fun addToCart(product: Product, quantity: Int) {
        _cartItems.add(product)
    }

    /**
     * Remove product from cart
     */
    fun removeFromCart(product: Product) {
        _cartItems.remove(product)
    }

    /**
     * Remove product by ID
     */
    fun removeFromCartById(productId: String) {
        _cartItems.removeAll { it.productId == productId }
    }

    /**
     * Clear entire cart
     */
    fun clearCart() {
        _cartItems.clear()
    }

    /**
     * Get total price
     */
    fun getTotalPrice(): Double {
        return _cartItems.sumOf { it.price }
    }

    /**
     * Get cart item count
     */
    fun getItemCount(): Int {
        return _cartItems.size
    }

    /**
     * Check if product is in cart
     */
    fun isInCart(productId: String): Boolean {
        return _cartItems.any { it.productId == productId }
    }
}
