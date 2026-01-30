package com.example.bunnix.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "products")
data class Product (
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val price: Double,
    val image_url: String,
    val description: String,
    val category: String,
    val location: String,
    val quantity: String,
    val vendor_id: String,
    val created_at: Long = System.currentTimeMillis()
)