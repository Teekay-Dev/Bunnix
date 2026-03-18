package com.example.bunnix.di

import com.example.bunnix.data.auth.AuthManager
import com.example.bunnix.data.repository.AuthRepositoryImpl
import com.example.bunnix.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing authentication dependencies.
 *
 * All Firebase and Auth-related objects are provided here.
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {


    /**
     * Provide AuthManager (wraps Firebase Auth operations)
     */
    @Provides
    @Singleton
    fun provideAuthManager(
        firebaseAuth: FirebaseAuth
    ): AuthManager {
        return AuthManager(firebaseAuth)
    }




}