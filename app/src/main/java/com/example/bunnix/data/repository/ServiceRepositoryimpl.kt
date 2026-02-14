package com.example.bunnix.data.repository


import android.net.Uri
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Service
import com.example.bunnix.domain.repository.ServiceRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ServiceRepository.
 * Handles Firestore service operations and Supabase image uploads.
 */
@Singleton
class ServiceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val supabase: SupabaseClient
) : ServiceRepository {

    companion object {
        private const val SERVICES_COLLECTION = "services"
        private const val SERVICE_IMAGES_BUCKET = "product-images"
    }

    override suspend fun addService(
        vendorId: String,
        vendorName: String,
        name: String,
        description: String,
        price: Double,
        duration: Int,
        category: String,
        imageUrl: String,
        availability: List<String>
    ): AuthResult<Service> {
        return try {
            // Generate service ID
            val serviceRef = firestore.collection(SERVICES_COLLECTION).document()
            val serviceId = serviceRef.id

            // Create service object
            val service = Service(
                serviceId = serviceId,
                vendorId = vendorId,
                vendorName = vendorName,
                name = name,
                description = description,
                price = price,
                duration = duration,
                category = category,
                imageUrl = imageUrl,
                availability = availability,
                totalBookings = 0,
                rating = 0.0,
                isActive = true,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )

            // Save to Firestore
            serviceRef.set(service).await()

            AuthResult.Success(service)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to add service",
                exception = e
            )
        }
    }

    override suspend fun updateService(
        serviceId: String,
        name: String?,
        description: String?,
        price: Double?,
        duration: Int?,
        category: String?,
        imageUrl: String?,
        availability: List<String>?,
        isActive: Boolean?
    ): AuthResult<Service> {
        return try {
            // Build update map
            val updates = mutableMapOf<String, Any>()
            name?.let { updates["name"] = it }
            description?.let { updates["description"] = it }
            price?.let { updates["price"] = it }
            duration?.let { updates["duration"] = it }
            category?.let { updates["category"] = it }
            imageUrl?.let { updates["imageUrl"] = it }
            availability?.let { updates["availability"] = it }
            isActive?.let { updates["isActive"] = it }
            updates["updatedAt"] = Timestamp.now()

            if (updates.isEmpty()) {
                return AuthResult.Error("No fields to update")
            }

            // Update Firestore
            firestore.collection(SERVICES_COLLECTION)
                .document(serviceId)
                .update(updates)
                .await()

            // Fetch and return updated service
            getService(serviceId)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to update service",
                exception = e
            )
        }
    }

    override suspend fun deleteService(serviceId: String): AuthResult<Unit> {
        return try {
            firestore.collection(SERVICES_COLLECTION)
                .document(serviceId)
                .delete()
                .await()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to delete service",
                exception = e
            )
        }
    }

    override suspend fun getService(serviceId: String): AuthResult<Service> {
        return try {
            val snapshot = firestore.collection(SERVICES_COLLECTION)
                .document(serviceId)
                .get()
                .await()

            val service = snapshot.toObject(Service::class.java)
                ?: throw Exception("Service not found")

            AuthResult.Success(service)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get service",
                exception = e
            )
        }
    }

    override suspend fun getVendorServices(vendorId: String): AuthResult<List<Service>> {
        return try {
            val snapshot = firestore.collection(SERVICES_COLLECTION)
                .whereEqualTo("vendorId", vendorId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val services = snapshot.toObjects(Service::class.java)

            AuthResult.Success(services)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get vendor services",
                exception = e
            )
        }
    }

    override suspend fun getServicesByCategory(
        category: String,
        limit: Int
    ): AuthResult<List<Service>> {
        return try {
            val snapshot = firestore.collection(SERVICES_COLLECTION)
                .whereEqualTo("category", category)
                .whereEqualTo("isActive", true)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val services = snapshot.toObjects(Service::class.java)

            AuthResult.Success(services)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to get services by category",
                exception = e
            )
        }
    }

    override suspend fun searchServices(
        query: String,
        limit: Int
    ): AuthResult<List<Service>> {
        return try {
            // Basic search implementation
            // For production, use Algolia or similar
            val snapshot = firestore.collection(SERVICES_COLLECTION)
                .whereEqualTo("isActive", true)
                .limit(limit.toLong())
                .get()
                .await()

            val allServices = snapshot.toObjects(Service::class.java)

            // Filter by name containing query (case-insensitive)
            val matchingServices = allServices.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }

            AuthResult.Success(matchingServices)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to search services",
                exception = e
            )
        }
    }

    override fun observeVendorServices(vendorId: String): Flow<List<Service>> = callbackFlow {
        val listener = firestore.collection(SERVICES_COLLECTION)
            .whereEqualTo("vendorId", vendorId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val services = snapshot?.toObjects(Service::class.java) ?: emptyList()
                trySend(services)
            }

        awaitClose {
            listener.remove()
        }
    }

    override suspend fun incrementServiceBookings(serviceId: String): AuthResult<Unit> {
        return try {
            val serviceRef = firestore.collection(SERVICES_COLLECTION).document(serviceId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(serviceRef)
                val currentBookings = snapshot.getLong("totalBookings") ?: 0
                transaction.update(serviceRef, "totalBookings", currentBookings + 1)
            }.await()

            AuthResult.Success(Unit)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to increment bookings",
                exception = e
            )
        }
    }

    override suspend fun uploadServiceImage(
        serviceId: String,
        imageUri: String
    ): AuthResult<String> {
        return try {
            val file = File(Uri.parse(imageUri).path ?: throw Exception("Invalid image URI"))
            val fileName = "${serviceId}_${System.currentTimeMillis()}.jpg"

            // Upload to Supabase Storage
            val bucket = supabase.storage.from(SERVICE_IMAGES_BUCKET)
            bucket.upload(fileName, file.readBytes())

            // Get public URL
            val publicUrl = bucket.publicUrl(fileName)

            AuthResult.Success(publicUrl)

        } catch (e: Exception) {
            AuthResult.Error(
                message = e.message ?: "Failed to upload service image",
                exception = e
            )
        }
    }
}
