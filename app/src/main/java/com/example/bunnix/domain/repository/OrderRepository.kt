package com.example.bunnix.domain.repository

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Order
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Order management.
 * Handles product orders, payment verification, and status updates.
 */
interface OrderRepository {

    /**
     * Create a new order (Customer places order)
     *
     * @param customerId Customer's user ID
     * @param customerName Customer's name
     * @param vendorId Vendor's user ID
     * @param vendorName Vendor's business name
     * @param items List of order items (product details, quantity, price)
     * @param totalAmount Total order amount
     * @param deliveryAddress Delivery address
     * @param paymentMethod Payment method (Bank Transfer, Cash, etc.)
     * @return AuthResult with created Order
     */
    suspend fun createOrder(
        customerId: String,
        customerName: String,
        vendorId: String,
        vendorName: String,
        items: List<Map<String, Any>>,
        totalAmount: Double,
        deliveryAddress: String,
        paymentMethod: String
    ): AuthResult<Order>

    /**
     * Upload payment receipt (Customer uploads transfer screenshot)
     *
     * @param orderId Order ID
     * @param receiptUri Local image URI
     * @return AuthResult with uploaded receipt URL
     */
    suspend fun uploadPaymentReceipt(
        orderId: String,
        receiptUri: String
    ): AuthResult<String>

    /**
     * Verify payment (Vendor confirms payment received)
     *
     * @param orderId Order ID
     * @param vendorId Vendor ID (for authorization)
     * @return AuthResult<Unit>
     */
    suspend fun verifyPayment(
        orderId: String,
        vendorId: String
    ): AuthResult<Unit>

    /**
     * Update order status
     *
     * @param orderId Order ID
     * @param newStatus New status (e.g., "Processing", "Shipped", "Delivered")
     * @param vendorId Vendor ID (for authorization)
     * @return AuthResult with updated Order
     */
    suspend fun updateOrderStatus(
        orderId: String,
        newStatus: String,
        vendorId: String
    ): AuthResult<Order>

    /**
     * Get a single order by ID
     *
     * @param orderId Order ID
     * @return AuthResult with Order data
     */
    suspend fun getOrder(orderId: String): AuthResult<Order>

    /**
     * Get customer's orders
     *
     * @param customerId Customer ID
     * @return AuthResult with list of Orders
     */
    suspend fun getCustomerOrders(customerId: String): AuthResult<List<Order>>

    /**
     * Get vendor's orders (orders placed with this vendor)
     *
     * @param vendorId Vendor ID
     * @return AuthResult with list of Orders
     */
    suspend fun getVendorOrders(vendorId: String): AuthResult<List<Order>>

    /**
     * Get pending orders for vendor (awaiting payment verification)
     *
     * @param vendorId Vendor ID
     * @return AuthResult with list of Orders
     */
    suspend fun getPendingOrders(vendorId: String): AuthResult<List<Order>>

    /**
     * Observe customer's orders in real-time
     *
     * @param customerId Customer ID
     * @return Flow of Order list
     */
    fun observeCustomerOrders(customerId: String): Flow<List<Order>>

    /**
     * Observe vendor's orders in real-time
     *
     * @param vendorId Vendor ID
     * @return Flow of Order list
     */
    fun observeVendorOrders(vendorId: String): Flow<List<Order>>

    /**
     * Cancel order (before vendor confirms payment)
     *
     * @param orderId Order ID
     * @param userId User ID (customer or vendor)
     * @return AuthResult<Unit>
     */
    suspend fun cancelOrder(
        orderId: String,
        userId: String
    ): AuthResult<Unit>

    fun getOrderById(orderId: String): Result<Order>
}
