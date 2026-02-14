package com.example.bunnix.domain.repository

import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Service
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Service management.
 * Handles CRUD operations for vendor services.
 */
interface ServiceRepository {

    /**
     * Add a new service
     *
     * @param vendorId Vendor's user ID
     * @param vendorName Vendor's business name
     * @param name Service name
     * @param description Service description
     * @param price Service price
     * @param duration Service duration in minutes
     * @param category Service category
     * @param imageUrl Service image URL
     * @param availability Available days/times
     * @return AuthResult with created Service
     */
    suspend fun addService(
        vendorId: String,
        vendorName: String,
        name: String,
        description: String,
        price: Double,
        duration: Int,
        category: String,
        imageUrl: String = "",
        availability: List<String> = emptyList()
    ): AuthResult<Service>

    /**
     * Update an existing service
     *
     * @param serviceId Service ID to update
     * @param name Updated name (optional)
     * @param description Updated description (optional)
     * @param price Updated price (optional)
     * @param duration Updated duration (optional)
     * @param category Updated category (optional)
     * @param imageUrl Updated image URL (optional)
     * @param availability Updated availability (optional)
     * @param isActive Service active status (optional)
     * @return AuthResult with updated Service
     */
    suspend fun updateService(
        serviceId: String,
        name: String? = null,
        description: String? = null,
        price: Double? = null,
        duration: Int? = null,
        category: String? = null,
        imageUrl: String? = null,
        availability: List<String>? = null,
        isActive: Boolean? = null
    ): AuthResult<Service>

    /**
     * Delete a service
     *
     * @param serviceId Service ID to delete
     * @return AuthResult<Unit> indicating success or failure
     */
    suspend fun deleteService(serviceId: String): AuthResult<Unit>

    /**
     * Get a single service by ID
     *
     * @param serviceId Service ID
     * @return AuthResult with Service data
     */
    suspend fun getService(serviceId: String): AuthResult<Service>

    /**
     * Get all services for a vendor
     *
     * @param vendorId Vendor ID
     * @return AuthResult with list of Services
     */
    suspend fun getVendorServices(vendorId: String): AuthResult<List<Service>>

    /**
     * Get services by category
     *
     * @param category Category name
     * @param limit Maximum number of services to return
     * @return AuthResult with list of Services
     */
    suspend fun getServicesByCategory(
        category: String,
        limit: Int = 20
    ): AuthResult<List<Service>>

    /**
     * Search services by name
     *
     * @param query Search query
     * @param limit Maximum results
     * @return AuthResult with list of Services
     */
    suspend fun searchServices(
        query: String,
        limit: Int = 20
    ): AuthResult<List<Service>>

    /**
     * Observe vendor's services in real-time
     *
     * @param vendorId Vendor ID
     * @return Flow of Service list
     */
    fun observeVendorServices(vendorId: String): Flow<List<Service>>

    /**
     * Increment service bookings counter
     * Called when a booking is confirmed
     *
     * @param serviceId Service ID
     * @return AuthResult<Unit>
     */
    suspend fun incrementServiceBookings(serviceId: String): AuthResult<Unit>

    /**
     * Upload service image to Supabase Storage
     *
     * @param serviceId Service ID
     * @param imageUri Local image URI
     * @return AuthResult with uploaded image URL
     */
    suspend fun uploadServiceImage(
        serviceId: String,
        imageUri: String
    ): AuthResult<String>
}