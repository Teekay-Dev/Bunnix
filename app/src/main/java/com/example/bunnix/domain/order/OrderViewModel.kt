package com.example.bunnix.domain.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.domain.uiState.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class OrderViewModel(
    private val createOrderUseCase: CreateOrderUseCase
) : ViewModel() {

    val state = MutableStateFlow<UiState<Order>>(UiState.Idle)

    fun onPlaceOrder(vendorId: String, items: List<String>) = viewModelScope.launch {
        state.value = UiState.Loading
        createOrderUseCase.execute(vendorId, items)
            .onSuccess { state.value = UiState.Success(it) }
            .onFailure { state.value = UiState.Error(it.message ?: "Order failed") }
    }
}
