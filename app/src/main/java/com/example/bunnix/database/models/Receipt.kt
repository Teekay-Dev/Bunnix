package com.example.bunnix.database.models

import com.google.firebase.Timestamp
import java.util.UUID

// ==================== RECEIPT ITEM ====================

data class ReceiptItem(
    val id: String = UUID.randomUUID().toString(),
    val productId: String = "",
    val name: String = "",
    val description: String? = null,
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val discount: Double = 0.0, // Percentage
    val taxRate: Double = 0.0, // Percentage (VAT)
    val imageUrl: String? = null
) {
    val subtotal: Double get() = quantity * unitPrice
    val discountAmount: Double get() = subtotal * (discount / 100)
    val taxableAmount: Double get() = subtotal - discountAmount
    val taxAmount: Double get() = taxableAmount * (taxRate / 100)
    val total: Double get() = taxableAmount + taxAmount
}

// ==================== PAYMENT DETAILS ====================

enum class PaymentMethod {
    CASH,
    CARD,
    UPI, // Paystack/Flutterwave reference
    BANK_TRANSFER,
    PAY_ON_DELIVERY
}

enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED,
    PARTIALLY_REFUNDED
}

data class PaymentDetails(
    val method: PaymentMethod = PaymentMethod.UPI,
    val transactionId: String = "",
    val reference: String = "", // Paystack/Flutterwave reference
    val status: PaymentStatus = PaymentStatus.PENDING,
    val paidAt: Timestamp? = null,
    val cardLastFour: String? = null,
    val cardBrand: String? = null, // Visa, Mastercard, Verve
    val bankName: String? = null, // For bank transfer
    val accountNumber: String? = null, // Masked
    val failureMessage: String? = null
)

// ==================== RECEIPT DATA CLASS ====================

data class Receipt(
    val id: String = UUID.randomUUID().toString(),
    val receiptNumber: String = "",
    val orderId: String = "", // Links to your OrderPlacedScreen orderId

    // Vendor Info (from VendorProfile)
    val vendorId: String = "",
    val vendorName: String = "",
    val vendorAddress: String = "",
    val vendorPhone: String = "",
    val vendorEmail: String = "",
    val vendorTaxId: String? = null,
    val vendorLogoUrl: String? = null,

    // Customer Info (from User)
    val customerId: String = "",
    val customerName: String = "",
    val customerEmail: String = "",
    val customerPhone: String = "",
    val customerAddress: String = "",
    val customerCity: String = "",
    val customerState: String = "",

    // Order Details
    val items: List<ReceiptItem> = emptyList(),
    val paymentDetails: PaymentDetails = PaymentDetails(),

    // Timestamps
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp? = null,

    // Additional Info
    val notes: String? = null,
    val deliveryNotes: String? = null,
    val subtotal: Double = 0.0,
    val totalDiscount: Double = 0.0,
    val totalTax: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val grandTotal: Double = 0.0
) {
    // Computed properties for safety
    val itemCount: Int get() = items.sumOf { it.quantity }
    val hasDiscount: Boolean get() = totalDiscount > 0
    val hasTax: Boolean get() = totalTax > 0
    val hasDeliveryFee: Boolean get() = deliveryFee > 0
}

// ==================== RECEIPT UI STATE ====================

sealed class ReceiptUiState {
    object Loading : ReceiptUiState()
    data class Success(val receipt: Receipt) : ReceiptUiState()
    object Empty : ReceiptUiState()
    data class Error(val message: String) : ReceiptUiState()
}