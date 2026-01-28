package com.example.bunnix.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage


object SupabaseClient {
    // We now reference BuildConfig, not the raw strings
    private const val URL = "https://acjfdgrgughthowlvxch.supabase.co"
    private const val KEY = "sb_publishable_COPpiGsuKoEHccGKT9VJRQ_ztP2LYVT"
    val client = createSupabaseClient(
        supabaseUrl = URL,
        supabaseKey = KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
        install(Realtime)
    }
}

