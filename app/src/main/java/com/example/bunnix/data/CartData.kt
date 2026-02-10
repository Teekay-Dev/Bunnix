package com.example.bunnix.data


import com.example.bunnix.model.Product

object CartData {
    val cartItems = mutableListOf<Product>()

    fun addToCart(product: Product) {
        cartItems.add(product)
    }

    fun clearCart() {
        cartItems.clear()
    }
}
