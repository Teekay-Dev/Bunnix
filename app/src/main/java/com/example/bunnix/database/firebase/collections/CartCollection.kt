package com.example.bunnix.database.firebase.collections

import com.example.bunnix.database.config.FirebaseConfig
import com.example.bunnix.database.models.CartItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object CartCollection {

    private val firestore = FirebaseConfig.firestore
    private fun getCartRef(userId: String) = firestore
        .collection("users")
        .document(userId)
        .collection("cart")

    // GET CART ITEMS (Real-time)
    fun getCartItems(userId: String): Flow<List<CartItem>> = callbackFlow {
        val listener = getCartRef(userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.toObjects(CartItem::class.java) ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    // ADD TO CART
    suspend fun addToCart(userId: String, item: CartItem): Result<Unit> {
        return try {
            getCartRef(userId).document(item.productId).set(item).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE QUANTITY
    suspend fun updateQuantity(userId: String, productId: String, newQuantity: Int): Result<Unit> {
        return try {
            if (newQuantity <= 0) {
                getCartRef(userId).document(productId).delete().await()
            } else {
                getCartRef(userId).document(productId).update("quantity", newQuantity).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // REMOVE FROM CART
    suspend fun removeFromCart(userId: String, productId: String): Result<Unit> {
        return try {
            getCartRef(userId).document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // CLEAR CART
    suspend fun clearCart(userId: String): Result<Unit> {
        return try {
            val batch = firestore.batch()
            val documents = getCartRef(userId).get().await()
            documents.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}