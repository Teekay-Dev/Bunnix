package com.example.bunnix.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.database.models.Service
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
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

                // Get vendor name from Firestore
                val vendorDoc = firestore.collection("vendorProfiles")
                    .document(vendorId)
                    .get()
                    .await()
                val vendorName = vendorDoc.getString("businessName") ?: "Unknown Vendor"

                // Upload image
                val imageUrl = uploadServiceImage(imageUri, vendorId)

                // Parse duration (e.g., "1 hour" -> 60 minutes)
                val durationMinutes = parseDurationToMinutes(duration)

                // Create service using the proper Service model
                val service = Service(
                    serviceId = "", // Firestore will auto-generate
                    vendorId = vendorId,
                    vendorName = vendorName,
                    name = name,
                    description = description,
                    price = price,
                    duration = durationMinutes,
                    category = category,
                    imageUrl = imageUrl,
                    availability = listOf("Mon-Fri: 9AM-5PM"), // Default
                    totalBookings = 0,
                    rating = 0.0,
                    isActive = true,
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )

                val docRef = firestore.collection("services")
                    .add(service)
                    .await()

                docRef.update("serviceId", docRef.id).await()

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
                    val imageUrl = uploadServiceImage(imageUri, vendorId)
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

    private suspend fun uploadServiceImage(imageUri: Uri, vendorId: String): String {
        val storageRef = storage.reference
            .child("services/$vendorId/service_${System.currentTimeMillis()}.jpg")

        val uploadTask = storageRef.putFile(imageUri)

        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
            _uploadProgress.value = progress / 100f
        }

        uploadTask.await()

        return storageRef.downloadUrl.await().toString()
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