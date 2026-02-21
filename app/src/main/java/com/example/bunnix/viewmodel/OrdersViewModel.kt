package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.domain.usecase.order.*
import com.example.bunnix.vendorUI.screens.vendor.orders.OrderItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrdersUiState(
    val isLoading: Boolean = false,
    val orders: List<OrderItem> = emptyList(),
    val selectedTab: Int = 0,
    val error: String? = null
)

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getVendorOrdersUseCase: GetVendorOrdersUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase,
    private val getOrderByIdUseCase: GetOrderByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState = _uiState.asStateFlow()

    private var currentVendorId: String = ""

    fun loadOrders(vendorId: String, status: String? = null) {
        currentVendorId = vendorId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getVendorOrdersUseCase(vendorId, status)
                .onSuccess { orders ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            orders = orders.map { order ->
                                OrderItem(
                                    orderId = order.orderId,
                                    orderNumber = order.orderNumber,
                                    customerName = order.customerName,
                                    totalAmount = order.totalAmount,
                                    status = order.status,
                                    items = order.items.size,
                                    date = order.createdAt?.toDate()?.toString() ?: "",
                                    paymentStatus = if (order.paymentVerified)
                                        "verified" else "awaiting_verification",
                                    paymentMethod = order.paymentMethod,
                                    paymentVerified = order.paymentVerified,
                                    vendorId = order.vendorId,
                                    vendorName = order.vendorName
                                )
                            }
                        )
                    }
                }
                .onFailure { error: Throwable ->  // Explicitly type the error parameter
                    _uiState.update {
                        it.copy(isLoading = false, error = error.message ?: "Unknown error")
                    }
                }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            updateOrderStatusUseCase(orderId, newStatus, currentVendorId)  // Add vendorId parameter
                .onSuccess { loadOrders(currentVendorId) }
                .onFailure { error: Throwable ->  // Explicitly type the error parameter
                    _uiState.update { it.copy(error = error.message ?: "Unknown error") }
                }
        }
    }

    fun verifyPayment(orderId: String) {
        viewModelScope.launch {
            updateOrderStatusUseCase(orderId, "payment_verified", currentVendorId)  // Add vendorId parameter
                .onSuccess { loadOrders(currentVendorId) }
                .onFailure { error: Throwable ->  // Explicitly type the error parameter
                    _uiState.update { it.copy(error = error.message ?: "Unknown error") }
                }
        }
    }

    private fun Unit.onFailure(function: (Throwable) -> Unit) {


    }
}