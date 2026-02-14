package com.example.bunnix.di

import com.example.bunnix.data.repository.ChatRepositoryImpl
import com.example.bunnix.data.repository.NotificationRepositoryImpl
import com.example.bunnix.domain.repository.ChatRepository
import com.example.bunnix.domain.repository.NotificationRepository
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.SupabaseClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatNotificationModule {

    @Provides
    @Singleton
    fun provideChatRepository(
        firestore: FirebaseFirestore,
        supabase: SupabaseClient
    ): ChatRepository {
        return ChatRepositoryImpl(firestore, supabase)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        firestore: FirebaseFirestore
    ): NotificationRepository {
        return NotificationRepositoryImpl(firestore)
    }
}
