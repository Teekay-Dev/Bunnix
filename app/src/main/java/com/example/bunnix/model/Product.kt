package com.example.bunnix.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val image_url: Int,
    val name: String,
    val description: String,
    val category: String,
    val price: String,
    val vendor_id: Int = 0,
    val quantity: Int,
    val location: String,
    val created_at: Long = System.currentTimeMillis()

)

