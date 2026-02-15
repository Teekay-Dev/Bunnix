package com.example.bunnix.di

import com.example.bunnix.data.repository.ProfileRepositoryImpl
import com.example.bunnix.domain.repository.ProfileRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileModule {

    @Provides
    @Singleton
    fun provideProfileRepository(
        firestore: FirebaseFirestore,
        supabase: SupabaseClient
    ): ProfileRepository {
        return ProfileRepositoryImpl(firestore, supabase)
    }
}