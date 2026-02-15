package com.example.bunnix.di

import com.example.bunnix.data.repository.ProductRepositoryImpl
import com.example.bunnix.data.repository.ServiceRepositoryImpl
import com.example.bunnix.domain.repository.ProductRepository
import com.example.bunnix.domain.repository.ServiceRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

/**
 * Hilt module for Product and Service repositories.
 * Provides dependencies for product/service management.
 */
@Module
@InstallIn(SingletonComponent::class)
object ProductServiceModule {

    @Provides
    @Singleton
    fun provideProductRepository(
        firestore: FirebaseFirestore,
        supabase: SupabaseClient
    ): ProductRepository {
        return ProductRepositoryImpl(firestore, supabase)
    }

    @Provides
    @Singleton
    fun provideServiceRepository(
        firestore: FirebaseFirestore,
        supabase: SupabaseClient
    ): ServiceRepository {
        return ServiceRepositoryImpl(firestore, supabase)
    }
}