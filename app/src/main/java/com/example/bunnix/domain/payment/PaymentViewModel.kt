package com.example.bunnix.domain.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.domain.uiState.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel(private val uploadUseCase: UploadPaymentProofUseCase) : ViewModel() {
    val state = MutableStateFlow<UiState<PaymentProof>>(UiState.Idle)

    fun onUploadReceipt(orderId: String, filePath: String) = viewModelScope.launch {
        state.value = UiState.Loading
        uploadUseCase.execute(orderId, filePath)
            .onSuccess { state.value = UiState.Success(it) }
            .onFailure { state.value = UiState.Error(it.message ?: "Upload failed") }
    }
}
