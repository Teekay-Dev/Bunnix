package com.example.bunnix.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class VendorService(
    val serviceId: String = "",
    val vendorId: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val duration: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val isAvailable: Boolean = true,
    val createdAt: Long = 0L
)

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _services = MutableStateFlow<List<VendorService>>(emptyList())
    val services: StateFlow<List<VendorService>> = _services.asStateFlow()

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

                        val servicesList = snapshot?.documents?.mapNotNull { doc ->
                            try {
                                VendorService(
                                    serviceId = doc.id,
                                    vendorId = doc.getString("vendorId") ?: "",
                                    name = doc.getString("name") ?: "",
                                    description = doc.getString("description") ?: "",
                                    price = doc.getDouble("price") ?: 0.0,
                                    duration = doc.getString("duration") ?: "",
                                    category = doc.getString("category") ?: "",
                                    imageUrl = doc.getString("imageUrl") ?: "",
                                    isAvailable = doc.getBoolean("isAvailable") ?: true,
                                    createdAt = doc.getLong("createdAt") ?: 0L
                                )
                            } catch (e: Exception) {
                                null
                            }
                        } ?: emptyList()

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

                // Upload image
                val imageUrl = uploadServiceImage(imageUri, vendorId)

                // Create service document
                val serviceData = hashMapOf(
                    "vendorId" to vendorId,
                    "name" to name,
                    "description" to description,
                    "price" to price,
                    "duration" to duration,
                    "category" to category,
                    "imageUrl" to imageUrl,
                    "isAvailable" to true,
                    "createdAt" to System.currentTimeMillis(),
                    "updatedAt" to System.currentTimeMillis()
                )

                firestore.collection("services")
                    .add(serviceData)
                    .await()

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

                val updateData = mutableMapOf<String, Any>(
                    "name" to name,
                    "description" to description,
                    "price" to price,
                    "duration" to duration,
                    "category" to category,
                    "updatedAt" to System.currentTimeMillis()
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

    fun toggleServiceAvailability(serviceId: String, isAvailable: Boolean) {
        viewModelScope.launch {
            try {
                firestore.collection("services")
                    .document(serviceId)
                    .update("isAvailable", isAvailable)
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

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}