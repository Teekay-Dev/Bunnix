//package com.example.bunnix.backend
//
//import androidx.lifecycle.ViewModel
//import com.example.bunnix.model.Vendor
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//
//class VendorProfileViewModel : ViewModel() {
//    private val _vendorState = MutableStateFlow<Vendor?>(null)
//    val vendorState = _vendorState.asStateFlow()
//
//    init {
//        fetchVendorProfile()
//    }
//
//    private fun fetchVendorProfile() {
//        // Match every parameter in your Vendor data class exactly
//        fun fetchVendorProfile() {
//            _vendorState.value = Vendor(
//                id = 1,
//                role = "Vendor",
//                firstName = "John",
//                surName = "Doe",
//                businessName = "Gourmet Bites",
//                email = "contact@gourmetbites.com",
//                phone = "+234 801 234 5678",
//                profileImage = 9,
//                createdAt = "2023-10-27",
//                about = "Specializing in gourmet meals and catering.",
//                rating = "4",   // If this errors, try "4" (String)
//                distance = "2.5 km", // If this errors, try 2 (Int)
//                isService = true,
//                category = "Catering"
//            )
//        }
//    }
//}