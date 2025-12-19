package com.example.bunnix.backend

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "products")
data class Product (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val image_url: String,
    val name: String,
    val description: String,
    val category: String,
    val price: String,
    val vendor_id: Int = 0,
    val quantity: String,
    val created_at: Long = System.currentTimeMillis()
)
