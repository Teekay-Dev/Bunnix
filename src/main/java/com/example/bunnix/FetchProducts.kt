package com.example.bunnix

import com.example.bunnix.database.DatabaseProvider

suspend fun fetchProducts(): List<Product> {
    return supabase
        .from("products")
        .select()
        .decodeList<Product>()
    val db = DatabaseProvider.getDatabase(context)
    val productDao = db.productDao()
}
