package com.example.bunnix.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

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
                    .get()
                    .await()
                    .documents.mapNotNull { doc ->
                        try {
                            VendorService(
                                serviceId = doc.id,
                                name = doc.getString("name") ?: "",
                                description = doc.getString("description") ?: "",
                                price = doc.getDouble("price") ?: 0.0,
                                duration = doc.getString("duration") ?: "",
                                category = doc.getString("category") ?: "",
                                imageUrl = doc.getString("imageUrl") ?: "",
                                isAvailable = doc.getBoolean("isAvailable") ?: true
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }.also { _services.value = it }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
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
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val vendorId = auth.currentUser?.uid ?: return@launch

                val imageUrl = imageUri?.let { uploadServiceImage(it) } ?: ""

                val serviceData = hashMapOf(
                    "vendorId" to vendorId,
                    "name" to name,
                    "description" to description,
                    "price" to price,
                    "duration" to duration,
                    "category" to category,
                    "imageUrl" to imageUrl,
                    "isAvailable" to true,
                    "createdAt" to FieldValue.serverTimestamp()
                )

                firestore.collection("services")
                    .add(serviceData)
                    .await()

                _successMessage.value = "Service added successfully!"
                loadServices()

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun uploadServiceImage(uri: Uri): String {
        return try {
            val vendorId = auth.currentUser?.uid ?: ""
            val filename = "services/${vendorId}/${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child(filename)
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            ""
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}

data class VendorService(
    val serviceId: String,
    val name: String,
    val description: String,
    val price: Double,
    val duration: String,
    val category: String,
    val imageUrl: String,
    val isAvailable: Boolean
)