package com.example.bunnix.database.firebase.collections

import com.example.bunnix.database.config.FirebaseConfig
import com.example.bunnix.database.models.Order
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

object OrderCollection {

    private val collection = FirebaseConfig.firestore.collection(FirebaseConfig.Collections.ORDERS)

    // CREATE ORDER (Customer Side)
    // Note: No payment receipt needed upfront.
    suspend fun createOrder(order: Order): Result<String> {
        return try {
            val orderNumber = generateOrderNumber()
            // Default status is "Order Placed"
            val orderData = order.copy(
                orderNumber = orderNumber,
                status = "Order Placed",
                createdAt = Timestamp.now()
            )
            val docRef = collection.add(orderData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GET CUSTOMER ORDERS (Real-time)
    fun getCustomerOrders(customerId: String): Flow<List<Order>> = callbackFlow {
        val listener = collection
            .whereEqualTo("customerId", customerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val orders = snapshot?.toObjects(Order::class.java) ?: emptyList()
                trySend(orders)
            }
        awaitClose { listener.remove() }
    }

    // GET SINGLE ORDER (For deep links from Chat)
    suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val snapshot = collection.document(orderId).get().await()
            val order = snapshot.toObject(Order::class.java)
            if (order != null) Result.success(order) else Result.failure(Exception("Order not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GET SINGLE ORDER (Real-time for Tracking)
    fun getOrderByIdFlow(orderId: String): Flow<Order?> = callbackFlow {
        val listener = collection.document(orderId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val order = snapshot?.toObject(Order::class.java)
                trySend(order)
            }
        awaitClose { listener.remove() }
    }

    // CANCEL ORDER (Customer can cancel if not yet Shipped)
    suspend fun cancelOrder(orderId: String, userId: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "status" to "Cancelled",
                "updatedAt" to Timestamp.now()
            )
            collection.document(orderId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateOrderNumber(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val date = dateFormat.format(Date())
        val random = (100000..999999).random()
        return "BNX-$date-$random"
    }
}