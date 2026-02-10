package com.example.bunnix.database

import com.example.bunnix.database.firebase.FirebaseManager
import com.example.bunnix.database.firebase.collections.*
import com.example.bunnix.database.supabase.SupabaseManager
import com.example.bunnix.database.supabase.storage.*

/**
 * Central access point for all database operations
 */
@Suppress("unused")
object BunnixDatabase {

    // Firebase Manager
    val auth = FirebaseManager

    // Firebase Collections
    val users = UserCollection
    val products = ProductCollection
    val services = ServiceCollection
    val orders = OrderCollection
    val bookings = BookingCollection
    val chats = ChatCollection
    val reviews = ReviewCollection
    val notifications = NotificationCollection
    val categories = CategoryCollection

    // Supabase Storage
    val storage = SupabaseManager
    val profileStorage = ProfileStorage
    val productStorage = ProductStorage
    val serviceStorage = ServiceStorage
    val paymentStorage = PaymentStorage
    val chatStorage = ChatStorage
    val reviewStorage = ReviewStorage
}