package com.example.bunnix.backend

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

interface ProductDao {
    @Query("SELECT * FROM products ORDER BY created_at DESC")
    fun getAllProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    @Query("SELECT * FROM products WHERE vendor_id = :vendorId")
    fun getProductsByVendor(vendorId: Int): Flow<List<Product>>


}