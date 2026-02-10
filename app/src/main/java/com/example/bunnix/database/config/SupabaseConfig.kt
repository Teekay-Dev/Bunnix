package com.example.bunnix.database.config

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

object SupabaseConfig {

    // Get these from your Supabase Dashboard → Settings → API
    private const val SUPABASE_URL = "https://rgrfhhudfhzuleqsizzo.supabase.co" // e.g., https://xxxxx.supabase.co
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJncmZoaHVkZmh6dWxlcXNpenpvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzA1MzE2NzQsImV4cCI6MjA4NjEwNzY3NH0.OmVJxD3IVXKEWgTGGcY2tNcIWa5PgGtX4yCwqcIQ4qU"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Storage)
        }
    }

    object Buckets {
        const val USER_PROFILES = "user-profiles"
        const val VENDOR_PHOTOS = "vendor-photos"
        const val PRODUCT_IMAGES = "product-images"
        const val SERVICE_IMAGES = "service-images"
        const val PAYMENT_RECEIPTS = "payment-receipts"
        const val CHAT_IMAGES = "chat-images"
        const val REVIEW_IMAGES = "review-images"
    }
}