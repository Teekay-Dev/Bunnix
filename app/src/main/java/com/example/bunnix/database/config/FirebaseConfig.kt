package com.example.bunnix.database.config

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseConfig {
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    object Collections {
        const val USERS = "users" 
        const val VENDOR_PROFILES = "vendorProfiles"
        const val PRODUCTS = "products"
        const val SERVICES = "services"
        const val ORDERS = "orders"
        const val BOOKINGS = "bookings"
        const val CHATS = "chats"
        const val MESSAGES = "messages"
        const val REVIEWS = "reviews"
        const val NOTIFICATIONS = "notifications"
        const val CATEGORIES = "categories"
    }
}