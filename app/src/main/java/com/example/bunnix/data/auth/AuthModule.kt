package com.example.bunnix.data.auth



import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
     * Provide Firebase Authentication instance
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    /**
     * Provide Firebase Firestore instance
     */
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

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

    /**
     * Provide AuthRepository implementation
     */
    @Provides
    @Singleton
    fun provideAuthRepository(
        authManager: AuthManager,
        firestore: FirebaseFirestore
    ): AuthRepository {
        return AuthRepositoryImpl(authManager, firestore)
    }
}
