package com.example.bunnix.di

import com.example.bunnix.data.repository.*
import com.example.bunnix.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // ✅ THIS WAS MISSING - It tells Hilt to use AuthRepositoryImpl when asked for AuthRepository
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds
    @Singleton
    abstract fun bindVendorRepository(impl: VendorRepositoryImpl): VendorRepository
}