package com.example.bunnix.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: ProductDatabase? = null

    fun getDatabase(context: Context): ProductDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                ProductDatabase::class.java,
                "bunnix_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
