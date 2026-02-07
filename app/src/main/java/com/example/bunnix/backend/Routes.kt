package com.example.bunnix.backend

object Routes {

    // ✅ Main Screens
    const val Home = "home"

    const val Chat = "chat"
    const val Notifications = "notifications"
    const val Profile = "profile"



    // ✅ Vendor
    const val VendorDetail = "vendor_detail/{vendorId}"

    // ✅ Lists
    const val ServiceList = "service_list"
    const val ProductList = "product_list"

    // ✅ Booking Flow
    const val Booking = "booking/{serviceName}/{price}"

    // ✅ Cart Flow
    const val Cart = "cart"

    // ✅ Checkout Flow
    const val Checkout = "checkout/{title}/{price}/{isProduct}"

    // ✅ Payment Flow
    const val Payment = "payment/{total}"

    // ✅ Final Confirmation Screen
    const val OrderSuccess = "order_success"

    // ✅ Tracking
    const val Tracking = "tracking/{orderId}"

    const val Search = "search/{query}"

}
