package com.example.bunnix.database.firebase.collections

import com.example.bunnix.database.config.FirebaseConfig
import com.example.bunnix.database.models.Product
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object ProductCollection {

    private val collection = FirebaseConfig.firestore.collection(FirebaseConfig.Collections.PRODUCTS)

    // GET ALL PRODUCTS (Real-time)
    fun getAllProducts(): Flow<List<Product>> = callbackFlow {
        val listener = collection
            .whereEqualTo("inStock", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.toObjects(Product::class.java) ?: emptyList()
                trySend(products)
            }
        awaitClose { listener.remove() }
    }

    // GET PRODUCTS BY VENDOR
    fun getProductsByVendor(vendorId: String): Flow<List<Product>> = callbackFlow {
        val listener = collection
            .whereEqualTo("vendorId", vendorId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.toObjects(Product::class.java) ?: emptyList()
                trySend(products)
            }
        awaitClose { listener.remove() }
    }

    // GET PRODUCTS BY CATEGORY
    fun getProductsByCategory(category: String): Flow<List<Product>> = callbackFlow {
        val listener = collection
            .whereEqualTo("category", category)
            .whereEqualTo("inStock", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.toObjects(Product::class.java) ?: emptyList()
                trySend(products)
            }
        awaitClose { listener.remove() }
    }

    // ADD PRODUCT
    suspend fun addProduct(product: Product): Result<String> {
        return try {
            val docRef = collection.add(product).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE PRODUCT
    suspend fun updateProduct(productId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            collection.document(productId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DELETE PRODUCT
    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            collection.document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}