package com.example.bunnix.data

import com.example.bunnix.R
import com.example.bunnix.model.Product

object ProductData {

    val products = listOf(

        Product(
            id = 1,
            image_url = R.drawable.shirt,
            name = "Classic T-Shirt",
            description = "Premium cotton shirt.",
            category = "Fashion",
            price = "3500",
            vendor_id = 1,
            quantity = 50,
            location = "Lagos"
        ),

        Product(
            id = 2,
            image_url = R.drawable.jeans,
            name = "Blue Jeans",
            description = "Comfortable stylish jeans.",
            category = "Fashion",
            price = "5000",
            vendor_id = 2,
            quantity = 10,
            location = "Abuja"
        ),

        Product(
            id = 3,
            image_url = R.drawable.sneakers,
            name = "White Sneakers",
            description = "Trendy everyday sneakers.",
            category = "Fashion",
            price = "8000",
            vendor_id = 3,
            quantity = 8,
            location = "Ibadan"
        ),

        Product(
            id = 4,
            image_url = R.drawable.phone,
            name = "iPhone 13 Pro",
            description = "Apple smartphone with amazing camera.",
            category = "Electronics",
            price = "450000",
            vendor_id = 4,
            quantity = 5,
            location = "Lagos"
        ),

        Product(
            id = 5,
            image_url = R.drawable.laptop,
            name = "Gaming Laptop",
            description = "High performance laptop for gaming.",
            category = "Electronics",
            price = "750000",
            vendor_id = 5,
            quantity = 3,
            location = "Abuja"
        )
    )
}