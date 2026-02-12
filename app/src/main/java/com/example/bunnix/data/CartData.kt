package com.example.bunnix.data


import com.example.bunnix.database.models.Product

object CartData {
    val cartItems = mutableListOf<Product>()

    fun addToCart(product: Product) {
        cartItems.add(product)
    }

    fun clearCart() {
        cartItems.clear()
    }
}
