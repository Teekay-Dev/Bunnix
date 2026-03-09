package com.example.bunnix.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.database.models.*
import com.example.bunnix.data.repository.ReceiptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiptViewModel @Inject constructor(
    private val receiptRepository: ReceiptRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReceiptUiState>(ReceiptUiState.Loading)
    val uiState: StateFlow<ReceiptUiState> = _uiState.asStateFlow()

    private val _currentReceipt = MutableStateFlow<Receipt?>(null)
    val currentReceipt: StateFlow<Receipt?> = _currentReceipt.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    /**
     * Load receipt by ID
     */
    fun loadReceipt(receiptId: String) {
        viewModelScope.launch {
            _uiState.value = ReceiptUiState.Loading
            delayForAnimation()

            receiptRepository.getReceipt(receiptId)
                .onSuccess { receipt ->
                    _currentReceipt.value = receipt
                    _uiState.value = ReceiptUiState.Success(receipt)
                }
                .onFailure { error ->
                    _uiState.value = ReceiptUiState.Error(error.message ?: "Failed to load receipt")
                }
        }
    }

    /**
     * Load receipt by Order ID (used from OrderPlacedScreen)
     */
    fun loadReceiptByOrderId(orderId: String) {
        viewModelScope.launch {
            _uiState.value = ReceiptUiState.Loading
            delayForAnimation()

            receiptRepository.getReceiptByOrderId(orderId)
                .onSuccess { receipt ->
                    _currentReceipt.value = receipt
                    _uiState.value = ReceiptUiState.Success(receipt)
                }
                .onFailure {
                    // If no receipt found, show empty state (order might be pending)
                    _uiState.value = ReceiptUiState.Empty
                }
        }
    }

    /**
     * Generate receipt after successful payment
     * Call this from PaymentMethodScreen after payment success
     */
    fun generateReceipt(
        orderId: String,
        paymentMethod: PaymentMethod,
        transactionId: String,
        status: PaymentStatus,
        reference: String = "",
        cardLastFour: String? = null,
        cardBrand: String? = null
    ) {
        viewModelScope.launch {
            _isGenerating.value = true

            val paymentDetails = PaymentDetails(
                method = paymentMethod,
                transactionId = transactionId,
                reference = reference,
                status = PaymentStatus.COMPLETED,
                paidAt = com.google.firebase.Timestamp.now(),
                cardLastFour = cardLastFour,
                cardBrand = cardBrand
            )

            receiptRepository.createReceiptFromOrder(orderId, paymentDetails)
                .onSuccess { receipt ->
                    _currentReceipt.value = receipt
                    _uiState.value = ReceiptUiState.Success(receipt)
                }
                .onFailure { error ->
                    _uiState.value = ReceiptUiState.Error(error.message ?: "Failed to generate receipt")
                }

            _isGenerating.value = false
        }
    }

    /**
     * Refresh current receipt
     */
    fun refresh() {
        _currentReceipt.value?.let { receipt ->
            loadReceipt(receipt.id)
        } ?: run {
            _uiState.value = ReceiptUiState.Empty
        }
    }

    /**
     * Check if receipt exists and load it, or show empty
     */
    fun checkAndLoadReceipt(orderId: String? = null, receiptId: String? = null) {
        viewModelScope.launch {
            _uiState.value = ReceiptUiState.Loading

            when {
                receiptId != null -> loadReceipt(receiptId)
                orderId != null -> loadReceiptByOrderId(orderId)
                else -> _uiState.value = ReceiptUiState.Empty
            }
        }
    }

    /**
     * Clear current state
     */
    fun clearState() {
        _uiState.value = ReceiptUiState.Loading
        _currentReceipt.value = null
    }

    private suspend fun delayForAnimation() {
        kotlinx.coroutines.delay(600) // For smooth loading animation
    }
}