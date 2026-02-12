package com.example.bunnix.frontend

import androidx.lifecycle.ViewModel
import com.example.bunnix.frontend.NetworkResult
import kotlinx.coroutines.delay

class VendorViewModel : ViewModel() {

    // ✅ Register User Function (Customer or Vendor)
    suspend fun registerUser(
        name: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        businessName: String? = null,
        businessAddress: String? = null,
        isVendor: Boolean
    ): NetworkResult<String> {

        // ✅ Basic Validation
        if (name.isBlank() || email.isBlank() || phone.isBlank()) {
            return NetworkResult.Error("Please fill all required fields")
        }

        if (password.length < 6) {
            return NetworkResult.Error("Password must be at least 6 characters")
        }

        if (password != confirmPassword) {
            return NetworkResult.Error("Passwords do not match")
        }

        // ✅ Vendor extra validation
        if (isVendor) {
            if (businessName.isNullOrBlank() || businessAddress.isNullOrBlank()) {
                return NetworkResult.Error("Please fill business details")
            }
        }

        // ✅ Fake API delay (simulate server call)
        delay(1500)

        // ✅ Success Result
        return NetworkResult.Success("Account Created Successfully")
    }
}
