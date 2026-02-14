package com.example.bunnix.data.repository

import android.net.Uri
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Order
import com.example.bunnix.domain.repository.OrderRepository
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * CORRECTED - Payment on Delivery Flow
 * Processing → Shipped → Delivered → Awaiting Payment → Payment Confirmed
 */
@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val supabase: SupabaseClient
) : OrderRepository {

    companion object {
        private const val ORDERS_COLLECTION = "orders"
        private const val PAYMENT_RECEIPTS_BUCKET = "payment-receipts"
    }

    override suspend fun createOrder(
        customerId: String,
        customerName: String,
        vendorId: String,
        vendorName: String,
        items: List<Map<String, Any>>,
        totalAmount: Double,
        deliveryAddress: String,
        paymentMethod: String
    ): AuthResult<Order> {
        return try {
            val orderRef = firestore.collection(ORDERS_COLLECTION).document()
            val orderId = orderRef.id

            // Order number format: BNX-YYYYMMDD-XXXXXX
            val currentDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
            val random = (100000..999999).random()
            val orderNumber = "BNX-$currentDate-$random"

            val order = Order(
                orderId = orderId,
                orderNumber = orderNumber,
                customerId = customerId,
                customerName = customerName,
                vendorId = vendorId,
                vendorName = vendorName,
                items = items,
                totalAmount = totalAmount,
                deliveryAddress = deliveryAddress,
                status = "Processing",  // CORRECTED: Start with Processing (Payment on Delivery)
                paymentMethod = paymentMethod,
                paymentReceiptUrl = "",
                paymentVerified = false,
                paymentVerifiedAt = null,
                statusHistory = listOf(
                    mapOf(
                        "status" to "Processing",
                        "timestamp" to Timestamp.now(),
                        "note" to "Order placed - Payment on delivery"
                    )
                ),
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )

            orderRef.set(order).await()

            AuthResult.Success(order)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to create order",
                exception = e
            )
        }
    }

    override suspend fun uploadPaymentReceipt(
        orderId: String,
        receiptUri: String
    ): AuthResult<String> {
        return try {
            val file = File(Uri.parse(receiptUri).path ?: throw Exception("Invalid URI"))
            val fileName = "${orderId}_receipt_${System.currentTimeMillis()}.jpg"

            val bucket = supabase.storage.from(PAYMENT_RECEIPTS_BUCKET)
            bucket.upload(fileName, file.readBytes())

            val publicUrl = bucket.publicUrl(fileName)

            // Get current order
            val orderSnapshot = firestore.collection(ORDERS_COLLECTION)
                .document(orderId)
                .get()
                .await()

            val order = orderSnapshot.toObject(Order::class.java)
                ?: throw Exception("Order not found")

            // Verify order has been delivered before accepting payment
            if (order.status != "Delivered") {
                throw Exception("Cannot upload payment receipt. Order must be delivered first.")
            }

            val newStatusHistory = order.statusHistory.toMutableList()
            newStatusHistory.add(
                mapOf(
                    "status" to "Awaiting Payment",
                    "timestamp" to Timestamp.now(),
                    "note" to "Customer uploaded payment receipt"
                )
            )

            // Update order with receipt URL and new status
            firestore.collection(ORDERS_COLLECTION)
                .document(orderId)
                .update(
                    mapOf(
                        "paymentReceiptUrl" to publicUrl,
                        "status" to "Awaiting Payment",  // CORRECTED: Payment after delivery
                        "statusHistory" to newStatusHistory,
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()

            AuthResult.Success(publicUrl)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to upload receipt",
                exception = e
            )
        }
    }

    override suspend fun verifyPayment(
        orderId: String,
        vendorId: String
    ): AuthResult<Unit> {
        return try {
            val orderSnapshot = firestore.collection(ORDERS_COLLECTION)
                .document(orderId)
                .get()
                .await()

            val order = orderSnapshot.toObject(Order::class.java)
                ?: throw Exception("Order not found")

            if (order.vendorId != vendorId) {
                throw Exception("Unauthorized: Not your order")
            }

            val newStatusHistory = order.statusHistory.toMutableList()
            newStatusHistory.add(
                mapOf(
                    "status" to "Payment Confirmed",
                    "timestamp" to Timestamp.now(),
                    "note" to "Payment verified by vendor"
                )
            )

            firestore.collection(ORDERS_COLLECTION)
                .document(orderId)
                .update(
                    mapOf(
                        "paymentVerified" to true,
                        "paymentVerifiedAt" to Timestamp.now(),
                        "status" to "Payment Confirmed",
                        "statusHistory" to newStatusHistory,
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to verify payment",
                exception = e
            )
        }
    }

    override suspend fun updateOrderStatus(
        orderId: String,
        newStatus: String,
        vendorId: String
    ): AuthResult<Order> {
        return try {
            val orderSnapshot = firestore.collection(ORDERS_COLLECTION)
                .document(orderId)
                .get()
                .await()

            val order = orderSnapshot.toObject(Order::class.java)
                ?: throw Exception("Order not found")

            if (order.vendorId != vendorId) {
                throw Exception("Unauthorized: Not your order")
            }

            val newStatusHistory = order.statusHistory.toMutableList()
            newStatusHistory.add(
                mapOf(
                    "status" to newStatus,
                    "timestamp" to Timestamp.now(),
                    "note" to "Status updated by vendor"
                )
            )

            firestore.collection(ORDERS_COLLECTION)
                .document(orderId)
                .update(
                    mapOf(
                        "status" to newStatus,
                        "statusHistory" to newStatusHistory,
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()

            getOrder(orderId)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to update status",
                exception = e
            )
        }
    }

    override suspend fun getOrder(orderId: String): AuthResult<Order> {
        return try {
            val snapshot = firestore.collection(ORDERS_COLLECTION)
                .document(orderId)
                .get()
                .await()

            val order = snapshot.toObject(Order::class.java)
                ?: throw Exception("Order not found")

            AuthResult.Success(order)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get order",
                exception = e
            )
        }
    }

    override suspend fun getCustomerOrders(customerId: String): AuthResult<List<Order>> {
        return try {
            val snapshot = firestore.collection(ORDERS_COLLECTION)
                .whereEqualTo("customerId", customerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.toObjects(Order::class.java)
            AuthResult.Success(orders)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get customer orders",
                exception = e
            )
        }
    }

    override suspend fun getVendorOrders(vendorId: String): AuthResult<List<Order>> {
        return try {
            val snapshot = firestore.collection(ORDERS_COLLECTION)
                .whereEqualTo("vendorId", vendorId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.toObjects(Order::class.java)
            AuthResult.Success(orders)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get vendor orders",
                exception = e
            )
        }
    }

    override suspend fun getPendingOrders(vendorId: String): AuthResult<List<Order>> {
        return try {
            val snapshot = firestore.collection(ORDERS_COLLECTION)
                .whereEqualTo("vendorId", vendorId)
                .whereEqualTo("paymentVerified", false)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val orders = snapshot.toObjects(Order::class.java)
            AuthResult.Success(orders)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get pending orders",
                exception = e
            )
        }
    }

    override fun observeCustomerOrders(customerId: String): Flow<List<Order>> = callbackFlow {
        val listener = firestore.collection(ORDERS_COLLECTION)
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

    override fun observeVendorOrders(vendorId: String): Flow<List<Order>> = callbackFlow {
        val listener = firestore.collection(ORDERS_COLLECTION)
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

    override suspend fun cancelOrder(orderId: String, userId: String): AuthResult<Unit> {
        return try {
            val orderSnapshot = firestore.collection(ORDERS_COLLECTION)
                .document(orderId)
                .get()
                .await()

            val order = orderSnapshot.toObject(Order::class.java)
                ?: throw Exception("Order not found")

            if (order.customerId != userId && order.vendorId != userId) {
                throw Exception("Unauthorized")
            }

            if (order.status == "Delivered" || order.status == "Payment Confirmed") {
                throw Exception("Cannot cancel: Order already completed/paid")
            }

            firestore.collection(ORDERS_COLLECTION)
                .document(orderId)
                .update(
                    mapOf(
                        "status" to "Cancelled",
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to cancel order",
                exception = e
            )
        }
    }
}
