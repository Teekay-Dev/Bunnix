package com.example.bunnix.data

import android.content.Context
import com.example.bunnix.database.DatabaseProvider
import com.example.bunnix.model.Product
import com.example.bunnix.SupabaseClient.supabase
import io.github.jan.supabase.postgrest.from

class ProductRepository(context: Context) {

    private val productDao =
        DatabaseProvider.getDatabase(context).productDao()

    suspend fun syncProducts(): List<Product> {

        val remoteProducts = supabase
            .from("products")
            .select()
            .decodeList<Product>()

        productDao.insertAll(remoteProducts)

        return productDao.getAllProducts()
    }
}
