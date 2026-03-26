package com.example.bunnix.database.config

import com.example.bunnix.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

object SupabaseConfig {

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
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