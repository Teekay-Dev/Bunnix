package com.example.bunnix.model

import com.example.bunnix.R

val vendorList = listOf(

    // ✅ 1. Fashion Vendor
    Vendor(
        id = 1,
        businessName = "Style Hub",
        category = "Fashion",
        coverImage = R.drawable.style,
        logoImage = R.drawable.hero_pic,
        rating = 4.8,
        reviewCount = 254,
        distance = "1.5 km",
        isService = true,
        about = "Trendy fashion boutique with premium styling services.",

        services = listOf(
            ServiceItem("Personal Styling", "1-on-1 session", "2 hrs", "$75"),
            ServiceItem("Wardrobe Upgrade", "Closet refresh", "1 hr", "$45")
        ),

        products = listOf(
            ProductItem("Summer Dress", "Light and breezy", "$45", R.drawable.dress),
            ProductItem("Leather Jacket", "Premium quality", "$90", R.drawable.jacket)
        ),

        reviews = listOf(
            ReviewItem("Sarah", "Excellent service!", 5, "2 days ago"),
            ReviewItem("Mike", "Very professional.", 4, "1 week ago")
        )
    ),

    // ✅ 2. Food Vendor
    Vendor(
        id = 2,
        businessName = "Tasty Bites",
        category = "Food",
        coverImage = R.drawable.bites,
        logoImage = R.drawable.bites_background_pic,
        rating = 4.7,
        reviewCount = 180,
        distance = "0.8 km",
        isService = false,
        about = "Fast meals, fresh ingredients, always delicious.",

        products = listOf(
            ProductItem("Burger Combo", "Burger + fries", "$12", R.drawable.hamburger),
            ProductItem("Pizza Box", "Cheesy delight", "$20", R.drawable.margherita_pizza),
            ProductItem("Chicken Wrap", "Spicy and tasty", "$10", R.drawable.chicken_wrap)
        ),

        reviews = listOf(
            ReviewItem("Joy", "Super tasty!", 5, "Yesterday"),
            ReviewItem("Daniel", "Quick delivery!", 4, "3 days ago")
        )
    ),

    // ✅ 3. Beauty Vendor
    Vendor(
        id = 3,
        businessName = "Glow Beauty Studio",
        category = "Beauty",
        coverImage = R.drawable.beauty,
        logoImage = R.drawable.beauty_background,
        rating = 4.9,
        reviewCount = 320,
        distance = "2.2 km",
        isService = true,
        about = "Luxury skincare, makeup, and spa treatments.",

        services = listOf(
            ServiceItem("Facial Treatment", "Deep skin cleansing", "1 hr", "$50"),
            ServiceItem("Makeup Session", "Full glam look", "1.5 hrs", "$70"),
            ServiceItem("Spa Massage", "Relaxing full massage", "2 hrs", "$90")
        ),

        products = listOf(
            ProductItem("Organic Face Cream", "Natural and organic skincare", "$35.99", R.drawable.face_cream),
        ),

        reviews = listOf(
            ReviewItem("Ada", "My skin is glowing!", 5, "4 days ago"),
            ReviewItem("Rita", "Best makeup artist!", 5, "1 week ago")
        )
    ),

    // ✅ 4. Tech Vendor
    Vendor(
        id = 4,
        businessName = "Tech World",
        category = "Tech",
        coverImage = R.drawable.tech,
        logoImage = R.drawable.tech_background,
        rating = 4.5,
        reviewCount = 140,
        distance = "3.0 km",
        isService = false,
        about = "Affordable gadgets and accessories.",

        products = listOf(
            ProductItem("Wireless Headset", "Noise cancelling", "$60", R.drawable.headphones),
            ProductItem("Smart Watch", "Fitness tracker", "$80", R.drawable.smart),
            ProductItem("Power Bank", "Fast charging 20,000mAh", "$35", R.drawable.powerbank)
        ),

        reviews = listOf(
            ReviewItem("Chris", "Great quality gadgets!", 5, "1 week ago")
        )
    ),

    // ✅ 5. Event Vendor
    Vendor(
        id = 5,
        businessName = "Party Perfect",
        category = "Events",
        coverImage = R.drawable.event,
        logoImage = R.drawable.event_background,
        rating = 4.6,
        reviewCount = 98,
        distance = "4.1 km",
        isService = true,
        about = "Event planning, decoration, and rentals.",

        services = listOf(
            ServiceItem("Birthday Setup", "Full decoration package", "3 hrs", "$150"),
            ServiceItem("Wedding Planning", "Premium planning", "1 day", "$500"),
            ServiceItem("DJ Booking", "Music + hype package", "5 hrs", "$200")
        ),

        reviews = listOf(
            ReviewItem("Blessing", "My wedding was perfect!", 5, "2 weeks ago")
        )
    ),

    // ✅ 6. Home Services Vendor
    Vendor(
        id = 6,
        businessName = "HomeFix Experts",
        category = "Home",
        coverImage = R.drawable.home,
        logoImage = R.drawable.home_background,
        rating = 4.4,
        reviewCount = 76,
        distance = "5.0 km",
        isService = true,
        about = "Reliable repairs, plumbing, and electrical work.",

        services = listOf(
            ServiceItem("Plumbing Repair", "Fix leaks fast", "1 hr", "$40"),
            ServiceItem("Electrical Fix", "Safe wiring repair", "2 hrs", "$65"),
            ServiceItem("Painting Service", "Modern home repaint", "1 day", "$120")
        ),

        reviews = listOf(
            ReviewItem("Emeka", "Very fast service!", 4, "5 days ago")
        )
    ),

    // ✅ 7. Photography Vendor
    Vendor(
        id = 7,
        businessName = "SnapShot Studio",
        category = "Photography",
        coverImage = R.drawable.photographer,
        logoImage = R.drawable.photo_back_ground,
        rating = 4.9,
        reviewCount = 210,
        distance = "2.9 km",
        isService = true,
        about = "Professional photoshoots for events and portraits.",

        services = listOf(
            ServiceItem("Birthday Shoot", "Outdoor + edits", "3 hrs", "$120"),
            ServiceItem("Wedding Coverage", "Full day package", "1 day", "$600")
        ),

        reviews = listOf(
            ReviewItem("Tomi", "Photos came out perfect!", 5, "3 days ago")
        )
    ),

    // ✅ 8. Fitness Vendor
    Vendor(
        id = 8,
        businessName = "FitLife Gym",
        category = "Fitness",
        coverImage = R.drawable.fitness,
        logoImage = R.drawable.fitness_background,
        rating = 4.7,
        reviewCount = 145,
        distance = "1.2 km",
        isService = true,
        about = "Personal training, gym sessions, and wellness programs.",

        services = listOf(
            ServiceItem("Personal Training", "1-on-1 workout", "1 hr", "$30"),
            ServiceItem("Yoga Class", "Relax + stretch", "1 hr", "$20")
        ),

        reviews = listOf(
            ReviewItem("Samuel", "Best gym in town!", 5, "1 week ago")
        )
    ),

    // ✅ 9. Bakery Vendor
    Vendor(
        id = 9,
        businessName = "Sweet Cravings Bakery",
        category = "Bakery",
        coverImage = R.drawable.bakery,
        logoImage = R.drawable.baker_background,
        rating = 4.8,
        reviewCount = 190,
        distance = "0.5 km",
        isService = false,
        about = "Fresh cakes, pastries, and custom desserts.",

        products = listOf(
            ProductItem("Chocolate Cake", "Rich + creamy", "$25", R.drawable.cakes),
            ProductItem("Cupcake Box", "6pcs assorted", "$15", R.drawable.cupcakes)
        ),

        reviews = listOf(
            ReviewItem("Peace", "The cake was heavenly!", 5, "2 days ago")
        )
    ),

    // ✅ 10. Auto Vendor
    Vendor(
        id = 10,
        businessName = "AutoCare Garage",
        category = "Automobile",
        coverImage = R.drawable.car_dealer,
        logoImage = R.drawable.car_background,
        rating = 4.5,
        reviewCount = 88,
        distance = "6.0 km",
        isService = true,
        about = "Car repairs, servicing, and maintenance.",

        services = listOf(
            ServiceItem("Oil Change", "Fast engine service", "45 mins", "$25"),
            ServiceItem("Car Diagnostics", "Full checkup", "1 hr", "$50")
        ),

        reviews = listOf(
            ReviewItem("Kelvin", "Very reliable mechanics!", 5, "1 week ago")
        )
    ),

    // ✅ 11. Education Vendor
    Vendor(
        id = 11,
        businessName = "TutorPro Academy",
        category = "Education",
        coverImage = R.drawable.tutor,
        logoImage = R.drawable.tutor_background,
        rating = 4.9,
        reviewCount = 300,
        distance = "2.0 km",
        isService = true,
        about = "Private tutoring and exam preparation.",

        services = listOf(
            ServiceItem("Math Tutor", "Secondary school level", "2 hrs", "$40"),
            ServiceItem("IELTS Coaching", "Full prep course", "1 month", "$200")
        ),

        reviews = listOf(
            ReviewItem("Aisha", "Helped me pass my exams!", 5, "3 weeks ago")
        )
    ),

    // ✅ 12. Music Vendor
    Vendor(
        id = 12,
        businessName = "SoundWave Music Store",
        category = "Music",
        coverImage = R.drawable.music_guy,
        logoImage = R.drawable.music_backgroud,
        rating = 4.6,
        reviewCount = 120,
        distance = "3.8 km",
        isService = false,
        about = "Instruments, speakers, and music accessories.",

        products = listOf(
            ProductItem("Bluetooth Speaker", "Deep bass sound", "$70", R.drawable.speacker),
            ProductItem("Electric Guitar", "Beginner friendly", "$150", R.drawable.guitar)
        ),

        reviews = listOf(
            ReviewItem("James", "Great instruments selection!", 5, "5 days ago")
        )
    )
)
