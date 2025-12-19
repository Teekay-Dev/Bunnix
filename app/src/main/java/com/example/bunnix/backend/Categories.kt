package com.example.bunnix.backend

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
    data class Categories(
        @PrimaryKey(autoGenerate = true)
        val category: String,
        val name: String
    )