package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Order
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
                            orders = orders.map { order -> order.toOrderItem() }
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, error = error.message ?: "Unknown error")
                    }
                }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            when (val result = updateOrderStatusUseCase(orderId, newStatus, currentVendorId)) {
                is AuthResult.Success -> loadOrders(currentVendorId)
                is AuthResult.Error -> _uiState.update {
                    it.copy(error = result.message ?: "Unknown error")
                }

                else -> Unit
            }
        }
    }

    fun verifyPayment(orderId: String) {
        viewModelScope.launch {
            when (val result =
                updateOrderStatusUseCase(orderId, "Payment Confirmed", currentVendorId)) {
                is AuthResult.Success -> loadOrders(currentVendorId)
                is AuthResult.Error -> _uiState.update {
                    it.copy(error = result.message ?: "Unknown error")
                }

                else -> Unit
            }
        }
    }

    private fun Order.toOrderItem() = OrderItem(
        orderId = orderId,
        orderNumber = orderNumber,
        customerName = customerName,
        total = totalAmount,
        status = status,
        items = items.map { it["name"]?.toString() ?: "" },
        customerImage = "",
        timeAgo = createdAt?.toDate()?.toString() ?: ""
    )
}