package com.example.bunnix.backend


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideProductRepository(dao: ProductDao): ProductRepository {
        return ProductRepository(dao)
    }


    @Provides
    @Singleton
    fun provideDataStoreManager() = DataStoreManager()
}
