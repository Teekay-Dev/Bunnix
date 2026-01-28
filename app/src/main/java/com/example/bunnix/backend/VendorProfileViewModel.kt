package com.example.bunnix.backend

import androidx.lifecycle.ViewModel
import com.example.bunnix.model.Vendor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VendorProfileViewModel : ViewModel() {
    // We use the Vendor data class you provided
    private val _vendorState = MutableStateFlow<Vendor?>(null)
    val vendorState = _vendorState.asStateFlow()

    init {
        fetchVendorProfile()
    }

    private fun fetchVendorProfile() {
        // In a real app, this data comes from your Room DB or API after signup
        _vendorState.value = Vendor(
            id = 1,
            role = "Vendor",
            firstName = "John",
            surName = "Doe",
            businessName = "Gourmet Bites",
            email = "contact@gourmetbites.com",
            phone = "+234 801 234 5678",
            profileImage = "", // Add URI/URL here
            createdAt = "2023-10-27"
        )
    }
}