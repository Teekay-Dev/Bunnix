package com.example.bunnix.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.database.models.Service
import com.example.bunnix.database.supabase.storage.ServiceStorage
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    @dagger.hilt.android.qualifiers.ApplicationContext
    private val context: android.content.Context
) : ViewModel() {

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun loadServices() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val vendorId = auth.currentUser?.uid ?: return@launch

                firestore.collection("services")
                    .whereEqualTo("vendorId", vendorId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            _error.value = error.message
                            return@addSnapshotListener
                        }

                        val servicesList = snapshot?.toObjects(Service::class.java) ?: emptyList()
                        _services.value = servicesList
                        _isLoading.value = false
                    }

            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun addService(
        name: String,
        description: String,
        price: Double,
        duration: String,
        category: String,
        imageUri: Uri
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _uploadProgress.value = 0f
                _error.value = null

                val vendorId = auth.currentUser?.uid ?: return@launch

                // 1. Get vendor name
                val vendorDoc = firestore.collection("vendorProfiles")
                    .document(vendorId)
                    .get()
                    .await()
                val vendorName = vendorDoc.getString("businessName") ?: "Unknown Vendor"

                // 2. Generate Firestore Document Reference FIRST
                // This is the fix: We get the ID now, not after upload.
                val docRef = firestore.collection("services").document()
                val serviceId = docRef.id

                // 3. Upload Image to Supabase using the REAL Firestore ID
                val imageUrl = ServiceStorage.uploadServiceImage(
                    context = context,
                    serviceId = serviceId, // Use real ID
                    imageUri = imageUri
                ).getOrThrow()

                // 4. Parse duration
                val durationMinutes = parseDurationToMinutes(duration)

                // 5. Create the Service object with the correct ID
                val service = Service(
                    serviceId = serviceId, // Set the ID we generated
                    vendorId = vendorId,
                    vendorName = vendorName,
                    name = name,
                    description = description,
                    price = price,
                    duration = durationMinutes,
                    category = category,
                    imageUrl = imageUrl,
                    availability = listOf("Mon-Fri: 9AM-5PM"),
                    totalBookings = 0,
                    rating = 0.0,
                    isActive = true,
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )

                // 6. Save to Firebase Firestore (Set the document, no need to update later)
                docRef.set(service).await()

                _successMessage.value = "Service added successfully"

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add service"
            } finally {
                _isLoading.value = false
                _uploadProgress.value = 0f
            }
        }
    }

    fun updateService(
        serviceId: String,
        name: String,
        description: String,
        price: Double,
        duration: String,
        category: String,
        imageUri: Uri? = null
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                val vendorId = auth.currentUser?.uid ?: return@launch
                val durationMinutes = parseDurationToMinutes(duration)

                val updateData = mutableMapOf<String, Any>(
                    "name" to name,
                    "description" to description,
                    "price" to price,
                    "duration" to durationMinutes,
                    "category" to category,
                    "updatedAt" to Timestamp.now()
                )

                if (imageUri != null) {
                    val imageUrl = ServiceStorage.uploadServiceImage(
                        context = context,
                        serviceId = serviceId,
                        imageUri = imageUri
                    ).getOrThrow()
                    updateData["imageUrl"] = imageUrl
                }

                firestore.collection("services")
                    .document(serviceId)
                    .update(updateData)
                    .await()

                _successMessage.value = "Service updated successfully"

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update service"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteService(serviceId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("services")
                    .document(serviceId)
                    .delete()
                    .await()

                _successMessage.value = "Service deleted successfully"

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete service"
            }
        }
    }

    fun toggleServiceAvailability(serviceId: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                firestore.collection("services")
                    .document(serviceId)
                    .update("isActive", isActive)
                    .await()

            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    private fun parseDurationToMinutes(duration: String): Int {
        return when {
            duration.contains("15 minutes") -> 15
            duration.contains("30 minutes") -> 30
            duration.contains("45 minutes") -> 45
            duration.contains("1 hour") -> 60
            duration.contains("1.5 hours") -> 90
            duration.contains("2 hours") -> 120
            duration.contains("3 hours") -> 180
            duration.contains("4 hours") -> 240
            duration.contains("Full Day") -> 480
            else -> 60 // Default 1 hour
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}