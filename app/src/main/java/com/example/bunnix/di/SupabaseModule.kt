package com.example.bunnix.di

import com.example.bunnix.BuildConfig.SUPABASE_URL
import com.example.bunnix.BuildConfig.SUPABASE_ANON_KEY
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
//import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.realtime.Realtime

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Auth)
            install(Realtime)
        }
    }
}