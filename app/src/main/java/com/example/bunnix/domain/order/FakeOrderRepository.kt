package com.example.bunnix.domain.order


class FakeOrderRepository : OrderRepository {

    private val orders = mutableListOf<Order>()

    override suspend fun create(order: Order): Order {
        orders.add(order)
        return order
    }

    override suspend fun getById(orderId: String): Order? {
        return orders.find { it.id == orderId }
    }

    override suspend fun update(order: Order): Order {
        val index = orders.indexOfFirst { it.id == order.id }
        if (index != -1) {
            orders[index] = order
        } else {
            throw IllegalStateException("Order not found")
        }
        return order
    }

    // Helper for payment verification
    suspend fun verifyPayment(orderId: String, vendorId: String): Boolean {
        val order = getById(orderId) ?: return false
        if (order.vendorId != vendorId) return false
        val updatedOrder = order.copy(status = OrderStatus.PAYMENT_CONFIRMED)
        update(updatedOrder)
        return true
    }

    // Helper for marking order as delivered (for review testing)
    suspend fun markDelivered(orderId: String) {
        val order = getById(orderId) ?: throw IllegalStateException("Order not found")
        val updatedOrder = order.copy(status = OrderStatus.DELIVERED)
        update(updatedOrder)
    }

    // Optional: list all orders (for testing/debugging)
    fun allOrders(): List<Order> = orders.toList()
}
