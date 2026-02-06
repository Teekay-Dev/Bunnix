package com.example.bunnix.model.domain.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.model.domain.order.Order
import com.example.bunnix.model.domain.uiState.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class VendorPaymentViewModel(private val verifyUseCase: VerifyPaymentUseCase) : ViewModel() {
    val state = MutableStateFlow<UiState<Order>>(UiState.Idle)

    fun onVerifyPayment(orderId: String) = viewModelScope.launch {
        state.value = UiState.Loading
        verifyUseCase.execute(orderId)
            .onSuccess { state.value = UiState.Success(it) }
            .onFailure { state.value = UiState.Error(it.message ?: "Verification failed") }
    }
}
