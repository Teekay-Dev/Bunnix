package com.example.bunnix.di

import com.example.bunnix.data.repository.BookingRepositoryImpl
import com.example.bunnix.data.repository.OrderRepositoryImpl
import com.example.bunnix.domain.repository.BookingRepository
import com.example.bunnix.domain.repository.OrderRepository
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.SupabaseClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OrderBookingModule {

    @Provides
    @Singleton
    fun provideOrderRepository(
        firestore: FirebaseFirestore,
        supabase: SupabaseClient
    ): OrderRepository {
        return OrderRepositoryImpl(firestore, supabase)
    }

    @Provides
    @Singleton
    fun provideBookingRepository(
        firestore: FirebaseFirestore,
        supabase: SupabaseClient
    ): BookingRepository {
        return BookingRepositoryImpl(firestore, supabase)
    }
}
