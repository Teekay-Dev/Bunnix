package com.example.bunnix.database.firebase.collections

import com.example.bunnix.database.models.Order
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object OrderCollection {
    private val db = FirebaseFirestore.getInstance()
    private val ordersRef = db.collection("orders")

    suspend fun createOrder(order: Order): String {
        val docRef = ordersRef.document()
        // Set the ID on the document
        val newOrder = order.copy(orderId = docRef.id)
        docRef.set(newOrder).await()
        return docRef.id
    }

    // ✅ NEW: Observe order in real-time (for Track Screen & Success Screen)
    fun getOrderByIdFlow(orderId: String): Flow<Order?> = callbackFlow {
        val listener = ordersRef.document(orderId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val order = snapshot?.toObject(Order::class.java)
            trySend(order)
        }
        awaitClose { listener.remove() }
    }
}