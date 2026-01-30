package com.example.bunnix.backend

import com.example.bunnix.model.Product
import com.example.bunnix.model.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale.filter

class CustomerRepository {
    // Fetch everything for the "All" category
    suspend fun fetchAllProducts(): List<Product> = withContext(Dispatchers.IO) {
        try {
            SupabaseClient.client.from("products").select().decodeList<Product>()
        } catch (e: Exception) { emptyList() }
    }

    // Fetch by specific category
    suspend fun fetchByCategory(category: String): List<Product> = withContext(Dispatchers.IO) {
        try {
            SupabaseClient.client.from("products")
                .select { filter { eq("category", category) } }
                .decodeList<Product>()
        } catch (e: Exception) { emptyList() }
    }
}