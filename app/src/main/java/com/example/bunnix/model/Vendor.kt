package com.example.bunnix.model

data class Vendor(
    val id: Int,
    val businessName: String,
    val category: String,
    val coverImage: Int,
    val logoImage: Int,
    val rating: Double,
    val reviewCount: Int,
    val distance: String,
    val isService: Boolean,
    val about: String,
    val services: List<ServiceItem> = emptyList(),
    val products: List<ProductItem> = emptyList(),
    val reviews: List<ReviewItem> = emptyList()
)





//data class Vendor (
//    val id: Int = 0,
//    val role: String,
//    val firstName: String,
//    val surName: String,
//    val businessName: String,
//    val email: String,
//    val phone: String,
//    val profileImage: Int,
//    val about: String, // ✅ ADDED
//    val rating: String,
//    val distance: String,
//    val isService: Boolean, // Important: True for "Bookings", False for "Products"
//    val category: String,
//    val createdAt: String,
//    val reviewCount: String = "0" // Add this for the image details
//)
