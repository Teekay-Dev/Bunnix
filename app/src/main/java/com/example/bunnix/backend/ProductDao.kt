package com.example.bunnix.backend

import androidx.room.*
import com.example.bunnix.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // Get all products (used for dashboard / default listing)
    @Query("SELECT * FROM products ORDER BY created_at DESC")
    fun getAllProducts(): Flow<List<Product>>

    // 🔍 SEARCH PRODUCTS (name OR category)
    @Query("""
        SELECT * FROM products
        WHERE 
            name LIKE '%' || :query || '%'
            OR category LIKE '%' || :query || '%'
        ORDER BY description DESC
    """)
    fun searchProducts(query: String): Flow<List<Product>>

    // Vendor-specific products
    @Query("SELECT * FROM products WHERE vendor_id = :vendor_id")
    fun getProductsByVendor(vendor_id: Int): Flow<List<Product>>

    // Insert or update product
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    // Delete product
    @Delete
    suspend fun deleteProduct(product: Product)

    // Clear table (optional – admin/debug)
    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
}
