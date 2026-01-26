package com.example.bunnix.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bunnix.model.Product

@Database(
    entities = [Product::class],
    version = 1,
    exportSchema = false
)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
