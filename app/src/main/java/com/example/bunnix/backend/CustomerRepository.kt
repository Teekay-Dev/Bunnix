package com.example.bunnix.backend

import com.example.bunnix.data.remote.SupabaseClient
import com.example.bunnix.model.Product
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CustomerRepository {

    // Fetch everything for the "Explore" tab
    suspend fun fetchAllProducts(): List<Product> = withContext(Dispatchers.IO) {
        try {
            SupabaseClient.client.from("products")
                .select()
                .decodeList<Product>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Filter by Category (using your 'category' field)
    suspend fun fetchByCategory(category: String): List<Product> = withContext(Dispatchers.IO) {
        try {
            SupabaseClient.client.from("products")
                .select {
                    filter { eq("category", category) }
                }.decodeList<Product>()
        } catch (e: Exception) {
            emptyList()
        }
    }
}