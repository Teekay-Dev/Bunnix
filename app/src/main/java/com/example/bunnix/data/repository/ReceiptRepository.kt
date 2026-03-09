package com.example.bunnix.data.repository


import com.example.bunnix.database.models.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceiptRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val receiptsCollection = firestore.collection("receipts")
    private val ordersCollection = firestore.collection("orders")

    companion object {
        private const val TAG = "ReceiptRepository"
    }

    // ==================== CREATE RECEIPT ====================

    /**
     * Creates a receipt from a completed order
     * Call this after successful payment in PaymentMethodScreen
     */
    suspend fun createReceiptFromOrder(
        orderId: String,
        paymentDetails: PaymentDetails
    ): Result<Receipt> = try {
        // Fetch order details
        val orderDoc = ordersCollection.document(orderId).get().await()

        if (!orderDoc.exists()) {
            throw Exception("Order not found")
        }

        // Generate receipt number: RCP-YYYYMMDD-XXXX
        val receiptNumber = generateReceiptNumber()

        val receipt = Receipt(
            receiptNumber = receiptNumber,
            orderId = orderId,

            // Vendor info from order
            vendorId = orderDoc.getString("vendorId") ?: "",
            vendorName = orderDoc.getString("vendorName") ?: "",
            vendorAddress = orderDoc.getString("vendorAddress") ?: "",
            vendorPhone = orderDoc.getString("vendorPhone") ?: "",
            vendorEmail = orderDoc.getString("vendorEmail") ?: "",

            // Customer info from order
            customerId = orderDoc.getString("customerId") ?: "",
            customerName = orderDoc.getString("customerName") ?: "",
            customerEmail = orderDoc.getString("customerEmail") ?: "",
            customerPhone = orderDoc.getString("customerPhone") ?: "",
            customerAddress = orderDoc.getString("customerAddress") ?: "",
            customerCity = orderDoc.getString("customerCity") ?: "",
            customerState = orderDoc.getString("customerState") ?: "",

            // Items from order
            items = parseOrderItems(orderDoc.get("items")),

            // Payment details from successful payment
            paymentDetails = paymentDetails,

            // Financials from order
            subtotal = orderDoc.getDouble("subtotal") ?: 0.0,
            totalDiscount = orderDoc.getDouble("totalDiscount") ?: 0.0,
            totalTax = orderDoc.getDouble("totalTax") ?: 0.0,
            deliveryFee = orderDoc.getDouble("deliveryFee") ?: 0.0,
            grandTotal = orderDoc.getDouble("grandTotal") ?: 0.0,

            createdAt = Timestamp.now()
        )

        // Save to Firestore
        receiptsCollection.document(receipt.id).set(receipt).await()

        // Update order with receipt reference
        ordersCollection.document(orderId).update(
            mapOf(
                "receiptId" to receipt.id,
                "receiptNumber" to receiptNumber,
                "status" to "completed"
            )
        ).await()

        Result.success(receipt)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ==================== FETCH RECEIPTS ====================

    /**
     * Get receipt by ID
     */
    suspend fun getReceipt(receiptId: String): Result<Receipt> = try {
        val doc = receiptsCollection.document(receiptId).get().await()

        if (doc.exists()) {
            val receipt = doc.toObject(Receipt::class.java)
            if (receipt != null) {
                Result.success(receipt)
            } else {
                Result.failure(Exception("Failed to parse receipt"))
            }
        } else {
            Result.failure(Exception("Receipt not found"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Get receipt by order ID
     */
    suspend fun getReceiptByOrderId(orderId: String): Result<Receipt> = try {
        val query = receiptsCollection
            .whereEqualTo("orderId", orderId)
            .limit(1)
            .get()
            .await()

        if (!query.isEmpty) {
            val receipt = query.documents[0].toObject(Receipt::class.java)
            if (receipt != null) {
                Result.success(receipt)
            } else {
                Result.failure(Exception("Failed to parse receipt"))
            }
        } else {
            Result.failure(Exception("No receipt found for this order"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Get latest receipt for a customer
     */
    suspend fun getLatestReceipt(customerId: String): Receipt? = try {
        val query = receiptsCollection
            .whereEqualTo("customerId", customerId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()

        if (!query.isEmpty) {
            query.documents[0].toObject(Receipt::class.java)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }

    /**
     * Get all receipts for a customer with pagination
     */
    fun getCustomerReceipts(customerId: String, limit: Long = 20): Flow<List<Receipt>> = flow {
        try {
            val query = receiptsCollection
                .whereEqualTo("customerId", customerId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()

            val receipts = query.toObjects(Receipt::class.java)
            emit(receipts)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    /**
     * Get all receipts for a vendor
     */
    fun getVendorReceipts(vendorId: String, limit: Long = 20): Flow<List<Receipt>> = flow {
        try {
            val query = receiptsCollection
                .whereEqualTo("vendorId", vendorId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()

            val receipts = query.toObjects(Receipt::class.java)
            emit(receipts)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // ==================== UPDATE OPERATIONS ====================

    /**
     * Update receipt status (for refunds, etc.)
     */
    suspend fun updatePaymentStatus(
        receiptId: String,
        status: PaymentStatus,
        notes: String? = null
    ): Result<Unit> = try {
        val updates = mutableMapOf<String, Any>(
            "paymentDetails.status" to status.name,
            "updatedAt" to Timestamp.now()
        )
        notes?.let { updates["notes"] = it }

        receiptsCollection.document(receiptId).update(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Add note to receipt
     */
    suspend fun addNote(receiptId: String, note: String): Result<Unit> = try {
        receiptsCollection.document(receiptId)
            .update("notes", note, "updatedAt", Timestamp.now())
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ==================== HELPERS ====================

    private fun generateReceiptNumber(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val date = dateFormat.format(Date())
        val random = (1000..9999).random()
        return "RCP-$date-$random"
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseOrderItems(itemsData: Any?): List<ReceiptItem> {
        return when (itemsData) {
            is List<*> -> {
                itemsData.mapNotNull { item ->
                    when (item) {
                        is Map<*, *> -> ReceiptItem(
                            productId = item["productId"] as? String ?: "",
                            name = item["name"] as? String ?: "",
                            description = item["description"] as? String,
                            quantity = (item["quantity"] as? Long)?.toInt() ?: 1,
                            unitPrice = (item["unitPrice"] as? Double) ?: 0.0,
                            discount = (item["discount"] as? Double) ?: 0.0,
                            taxRate = (item["taxRate"] as? Double) ?: 0.0,
                            imageUrl = item["imageUrl"] as? String
                        )
                        else -> null
                    }
                }
            }
            else -> emptyList()
        }
    }

    /**
     * Check if receipt exists for order
     */
    suspend fun receiptExistsForOrder(orderId: String): Boolean = try {
        val query = receiptsCollection
            .whereEqualTo("orderId", orderId)
            .limit(1)
            .get()
            .await()
        !query.isEmpty
    } catch (e: Exception) {
        false
    }
}