package com.example.bunnix

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

object SupabaseClient {

    val supabase = createSupabaseClient(
        supabaseUrl = "https://uvqguevyvoclpimjvezk.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InV2cWd1ZXZ5dm9jbHBpbWp2ZXprIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Njg2NjcxMTUsImV4cCI6MjA4NDI0MzExNX0.URMgGFMzkzrenqBR6hxjt2fJ8iRkXmKzbtnitTcLjsQ"
    ) {
        install(Postgrest)
    }
}
