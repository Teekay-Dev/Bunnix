package com.example.bunnix.data.repository

import android.net.Uri
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Product
import com.example.bunnix.domain.repository.ProductRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ProductRepository.
 * Handles Firestore product operations and Supabase image uploads.
 */
@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val supabase: SupabaseClient
) : ProductRepository {

    companion object {
        private const val PRODUCTS_COLLECTION = "products"
        private const val PRODUCT_IMAGES_BUCKET = "product-images"
    }

    override suspend fun addProduct(
        vendorId: String,
        vendorName: String,
        name: String,
        description: String,
        price: Double,
        category: String,
        imageUrls: List<String>,
        totalStock: Int,
        discountPrice: Double?,
        variants: List<Map<String, Any>>,
        tags: List<String>
    ): AuthResult<Product> {
        return try {
            // Generate product ID
            val productRef = firestore.collection(PRODUCTS_COLLECTION).document()
            val productId = productRef.id

            // Create product object
            val product = Product(
                productId = productId,
                vendorId = vendorId,
                vendorName = vendorName,
                name = name,
                description = description,
                price = price,
                discountPrice = discountPrice,
                category = category,
                imageUrls = imageUrls,
                variants = variants,
                totalStock = totalStock,
                inStock = totalStock > 0,
                tags = tags,
                views = 0,
                sold = 0,
                createdAt = Timestamp.Companion.now(),
                updatedAt = Timestamp.Companion.now()
            )

            // Save to Firestore
            productRef.set(product).await()

            AuthResult.Success(product)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to add product",
                exception = e
            )
        }
    }

    override suspend fun updateProduct(
        productId: String,
        name: String?,
        description: String?,
        price: Double?,
        category: String?,
        imageUrls: List<String>?,
        totalStock: Int?,
        discountPrice: Double?,
        variants: List<Map<String, Any>>?,
        tags: List<String>?,
        inStock: Boolean?
    ): AuthResult<Product> {
        return try {
            // Build update map
            val updates = mutableMapOf<String, Any>()
            name?.let { updates["name"] = it }
            description?.let { updates["description"] = it }
            price?.let { updates["price"] = it }
            category?.let { updates["category"] = it }
            imageUrls?.let { updates["imageUrls"] = it }
            totalStock?.let {
                updates["totalStock"] = it
                updates["inStock"] = it > 0
            }
            discountPrice?.let { updates["discountPrice"] = it }
            variants?.let { updates["variants"] = it }
            tags?.let { updates["tags"] = it }
            inStock?.let { updates["inStock"] = it }
            updates["updatedAt"] = Timestamp.Companion.now()

            if (updates.isEmpty()) {
                return AuthResult.Error("No fields to update")
            }

            // Update Firestore
            firestore.collection(PRODUCTS_COLLECTION)
                .document(productId)
                .update(updates)
                .await()

            // Fetch and return updated product
            getProduct(productId)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to update product",
                exception = e
            )
        }
    }

    override suspend fun deleteProduct(productId: String): AuthResult<Unit> {
        return try {
            firestore.collection(PRODUCTS_COLLECTION)
                .document(productId)
                .delete()
                .await()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to delete product",
                exception = e
            )
        }
    }

    override suspend fun getProduct(productId: String): AuthResult<Product> {
        return try {
            val snapshot = firestore.collection(PRODUCTS_COLLECTION)
                .document(productId)
                .get()
                .await()

            val product = snapshot.toObject(Product::class.java)
                ?: throw Exception("Product not found")

            AuthResult.Success(product)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get product",
                exception = e
            )
        }
    }

    override suspend fun getVendorProducts(vendorId: String): AuthResult<List<Product>> {
        return try {
            val snapshot = firestore.collection(PRODUCTS_COLLECTION)
                .whereEqualTo("vendorId", vendorId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val products = snapshot.toObjects(Product::class.java)

            AuthResult.Success(products)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get vendor products",
                exception = e
            )
        }
    }

    override suspend fun getProductsByCategory(
        category: String,
        limit: Int
    ): AuthResult<List<Product>> {
        return try {
            val snapshot = firestore.collection(PRODUCTS_COLLECTION)
                .whereEqualTo("category", category)
                .whereEqualTo("inStock", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val products = snapshot.toObjects(Product::class.java)

            AuthResult.Success(products)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get products by category",
                exception = e
            )
        }
    }

    override suspend fun searchProducts(
        query: String,
        limit: Int
    ): AuthResult<List<Product>> {
        return try {
            // Note: Firestore doesn't support full-text search
            // This is a basic implementation using array-contains
            // For production, consider using Algolia or Elasticsearch
            val snapshot = firestore.collection(PRODUCTS_COLLECTION)
                .whereArrayContains("tags", query.lowercase())
                .whereEqualTo("inStock", true)
                .limit(limit.toLong())
                .get()
                .await()

            val products = snapshot.toObjects(Product::class.java)

            AuthResult.Success(products)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to search products",
                exception = e
            )
        }
    }

    override fun observeVendorProducts(vendorId: String): Flow<List<Product>> = callbackFlow {
        val listener = firestore.collection(PRODUCTS_COLLECTION)
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

        awaitClose {
            listener.remove()
        }
    }

    override suspend fun updateProductStock(
        productId: String,
        newStock: Int
    ): AuthResult<Unit> {
        return try {
            firestore.collection(PRODUCTS_COLLECTION)
                .document(productId)
                .update(
                    mapOf(
                        "totalStock" to newStock,
                        "inStock" to (newStock > 0),
                        "updatedAt" to Timestamp.Companion.now()
                    )
                )
                .await()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to update stock",
                exception = e
            )
        }
    }

    override suspend fun incrementProductViews(productId: String): AuthResult<Unit> {
        return try {
            val productRef = firestore.collection(PRODUCTS_COLLECTION).document(productId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(productRef)
                val currentViews = snapshot.getLong("views") ?: 0
                transaction.update(productRef, "views", currentViews + 1)
            }.await()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to increment views",
                exception = e
            )
        }
    }

    override suspend fun uploadProductImages(
        productId: String,
        imageUris: List<String>
    ): AuthResult<List<String>> {
        return try {
            val uploadedUrls = mutableListOf<String>()

            imageUris.forEachIndexed { index, uri ->
                val file = File(Uri.parse(uri).path ?: throw Exception("Invalid image URI"))
                val fileName = "${productId}_${index}_${System.currentTimeMillis()}.jpg"

                // Upload to Supabase Storage
                val bucket = supabase.storage.from(PRODUCT_IMAGES_BUCKET)
                bucket.upload(fileName, file.readBytes())

                // Get public URL
                val publicUrl = bucket.publicUrl(fileName)
                uploadedUrls.add(publicUrl)
            }

            AuthResult.Success(uploadedUrls)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to upload product images",
                exception = e
            )
        }
    }
}