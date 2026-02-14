package com.example.bunnix.domain.repository

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.User
import com.example.bunnix.database.models.VendorProfile
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for User and Vendor profile management.
 * Handles profile updates, vendor profile creation, and profile fetching.
 */
interface ProfileRepository {

    /**
     * Update user profile information
     *
     * @param userId User ID
     * @param name Updated name (optional)
     * @param phone Updated phone (optional)
     * @param address Updated address (optional)
     * @param city Updated city (optional)
     * @param state Updated state (optional)
     * @param profilePicUrl Updated profile picture URL (optional)
     * @return AuthResult with updated User
     */
    suspend fun updateUserProfile(
        userId: String,
        name: String? = null,
        phone: String? = null,
        address: String? = null,
        city: String? = null,
        state: String? = null,
        profilePicUrl: String? = null
    ): AuthResult<User>

    /**
     * Get user profile by ID
     *
     * @param userId User ID
     * @return AuthResult with User data
     */
    suspend fun getUserProfile(userId: String): AuthResult<User>

    /**
     * Observe user profile changes in real-time
     *
     * @param userId User ID
     * @return Flow of User data
     */
    fun observeUserProfile(userId: String): Flow<User?>

    /**
     * Update vendor profile information
     *
     * @param vendorId Vendor ID
     * @param businessName Updated business name (optional)
     * @param description Updated description (optional)
     * @param category Updated category (optional)
     * @param subCategories Updated subcategories (optional)
     * @param address Updated address (optional)
     * @param phone Updated phone (optional)
     * @param email Updated email (optional)
     * @param coverPhotoUrl Updated cover photo URL (optional)
     * @param bankName Bank name for payments (optional)
     * @param accountNumber Account number (optional)
     * @param accountName Account holder name (optional)
     * @param alternativePayment Alternative payment method (optional)
     * @param workingHours Updated working hours map (optional)
     * @param isAvailable Availability status (optional)
     * @return AuthResult with updated VendorProfile
     */
    suspend fun updateVendorProfile(
        vendorId: String,
        businessName: String? = null,
        description: String? = null,
        category: String? = null,
        subCategories: List<String>? = null,
        address: String? = null,
        phone: String? = null,
        email: String? = null,
        coverPhotoUrl: String? = null,
        bankName: String? = null,
        accountNumber: String? = null,
        accountName: String? = null,
        alternativePayment: String? = null,
        workingHours: Map<String, String>? = null,
        isAvailable: Boolean? = null
    ): AuthResult<VendorProfile>

    /**
     * Get vendor profile by ID
     *
     * @param vendorId Vendor ID
     * @return AuthResult with VendorProfile data
     */
    suspend fun getVendorProfile(vendorId: String): AuthResult<VendorProfile>

    /**
     * Observe vendor profile changes in real-time
     *
     * @param vendorId Vendor ID
     * @return Flow of VendorProfile data
     */
    fun observeVendorProfile(vendorId: String): Flow<VendorProfile?>

    /**
     * Create vendor profile for existing user
     * Used when customer wants to "Become a Vendor"
     *
     * @param userId User ID
     * @param businessName Business name
     * @param businessAddress Business address
     * @param category Business category
     * @return AuthResult with created VendorProfile
     */
    suspend fun createVendorProfile(
        userId: String,
        businessName: String,
        businessAddress: String,
        category: String
    ): AuthResult<VendorProfile>

    /**
     * Upload profile picture to Supabase Storage
     *
     * @param userId User ID
     * @param imageUri Local image URI
     * @return AuthResult with uploaded image URL
     */
    suspend fun uploadProfilePicture(
        userId: String,
        imageUri: String
    ): AuthResult<String>

    /**
     * Upload vendor cover photo to Supabase Storage
     *
     * @param vendorId Vendor ID
     * @param imageUri Local image URI
     * @return AuthResult with uploaded image URL
     */
    suspend fun uploadCoverPhoto(
        vendorId: String,
        imageUri: String
    ): AuthResult<String>
}