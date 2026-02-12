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

    // CREATE ORDER
    suspend fun createOrder(order: Order): Result<String> {
        return try {
            val orderNumber = generateOrderNumber()
            val orderData = order.copy(orderNumber = orderNumber)
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

    // GET VENDOR ORDERS (Real-time)
    fun getVendorOrders(vendorId: String): Flow<List<Order>> = callbackFlow {
        val listener = collection
            .whereEqualTo("vendorId", vendorId)
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

    // UPLOAD PAYMENT RECEIPT
    suspend fun uploadPaymentReceipt(
        orderId: String,
        receiptUrl: String,
        paymentMethod: String
    ): Result<Unit> {
        return try {
            val updates = mapOf(
                "paymentReceiptUrl" to receiptUrl,
                "paymentMethod" to paymentMethod,
                "status" to "Payment Submitted",
                "updatedAt" to Timestamp.now()
            )
            collection.document(orderId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // VERIFY PAYMENT (Vendor confirms)
    suspend fun verifyPayment(orderId: String, vendorId: String): Result<Unit> {
        return try {
            val statusUpdate = mapOf(
                "status" to "Payment Confirmed",
                "timestamp" to Timestamp.now(),
                "updatedBy" to vendorId
            )

            val updates = mapOf(
                "paymentVerified" to true,
                "paymentVerifiedAt" to Timestamp.now(),
                "status" to "Payment Confirmed",
                "statusHistory" to FieldValue.arrayUnion(statusUpdate),
                "updatedAt" to Timestamp.now()
            )

            collection.document(orderId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE ORDER STATUS
    suspend fun updateOrderStatus(orderId: String, newStatus: String, userId: String): Result<Unit> {
        return try {
            val statusUpdate = mapOf(
                "status" to newStatus,
                "timestamp" to Timestamp.now(),
                "updatedBy" to userId
            )

            val updates = mutableMapOf<String, Any>(
                "status" to newStatus,
                "statusHistory" to FieldValue.arrayUnion(statusUpdate),
                "updatedAt" to Timestamp.now()
            )

            if (newStatus == "Delivered") {
                updates["completedAt"] = Timestamp.now()
            }

            collection.document(orderId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GENERATE ORDER NUMBER
    private fun generateOrderNumber(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val date = dateFormat.format(Date())
        val random = (100000..999999).random()
        return "BNX-$date-$random"
    }
}