package com.example.bunnix.di

import android.content.Context
import com.example.bunnix.domain.manager.UserModeManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for UserModeManager.
 * Provides singleton instance for mode management.
 */
@Module
@InstallIn(SingletonComponent::class)
object UserModeModule {

    @Provides
    @Singleton
    fun provideUserModeManager(
        @ApplicationContext context: Context
    ): UserModeManager {
        return UserModeManager(context)
    }
}