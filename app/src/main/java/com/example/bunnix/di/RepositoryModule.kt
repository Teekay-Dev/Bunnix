package com.example.bunnix.di

import com.example.bunnix.data.repository.VendorRepositoryImpl
import com.example.bunnix.domain.repository.VendorRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVendorRepository(
        impl: VendorRepositoryImpl
    ): VendorRepository
}