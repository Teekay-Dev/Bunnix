package com.example.bunnix.utils

object Constants {
    const val DATABASE_NAME = "bunnix_db"
    const val PREFERENCES_NAME = "bunnix_prefs"

    // Order Status
    const val ORDER_STATUS_PENDING = "pending"
    const val ORDER_STATUS_PROCESSING = "processing"
    const val ORDER_STATUS_SHIPPED = "shipped"
    const val ORDER_STATUS_DELIVERED = "delivered"
    const val ORDER_STATUS_CANCELLED = "cancelled"

    // Booking Status
    const val BOOKING_STATUS_REQUESTED = "requested"
    const val BOOKING_STATUS_CONFIRMED = "confirmed"
    const val BOOKING_STATUS_IN_PROGRESS = "in_progress"
    const val BOOKING_STATUS_COMPLETED = "completed"
    const val BOOKING_STATUS_CANCELLED = "cancelled"

    // Payment Status
    const val PAYMENT_STATUS_AWAITING = "awaiting_verification"
    const val PAYMENT_STATUS_VERIFIED = "verified"
    const val PAYMENT_STATUS_FAILED = "failed"

    // Notification Types
    const val NOTIFICATION_TYPE_ORDER = "order"
    const val NOTIFICATION_TYPE_PAYMENT = "payment"
    const val NOTIFICATION_TYPE_MESSAGE = "message"
    const val NOTIFICATION_TYPE_BOOKING = "booking"

    // Collection Names
    const val COLLECTION_USERS = "users"
    const val COLLECTION_VENDORS = "vendorProfiles"
    const val COLLECTION_PRODUCTS = "products"
    const val COLLECTION_SERVICES = "services"
    const val COLLECTION_ORDERS = "orders"
    const val COLLECTION_BOOKINGS = "bookings"
    const val COLLECTION_CHATS = "chats"
    const val COLLECTION_MESSAGES = "messages"
    const val COLLECTION_REVIEWS = "reviews"
    const val COLLECTION_NOTIFICATIONS = "notifications"

    // Storage Buckets
    const val STORAGE_PROFILES = "user-profiles"
    const val STORAGE_VENDOR_PHOTOS = "vendor-photos"
    const val STORAGE_PRODUCT_IMAGES = "product-images"
    const val STORAGE_SERVICE_IMAGES = "service-images"
    const val STORAGE_PAYMENT_RECEIPTS = "payment-receipts"
    const val STORAGE_CHAT_IMAGES = "chat-images"
    const val STORAGE_REVIEW_IMAGES = "review-images"
}