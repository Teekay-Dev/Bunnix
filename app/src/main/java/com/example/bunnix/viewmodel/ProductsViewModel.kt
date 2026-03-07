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
class ProductsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _products = MutableStateFlow<List<VendorProduct>>(emptyList())
    val products: StateFlow<List<VendorProduct>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress.asStateFlow()

    fun loadProducts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val vendorId = auth.currentUser?.uid ?: return@launch

                firestore.collection("products")
                    .whereEqualTo("vendorId", vendorId)
                    .get()
                    .await()
                    .documents.mapNotNull { doc ->
                        try {
                            VendorProduct(
                                productId = doc.id,
                                name = doc.getString("name") ?: "",
                                description = doc.getString("description") ?: "",
                                price = doc.getDouble("price") ?: 0.0,
                                quantity = doc.getLong("quantity")?.toInt() ?: 0,
                                category = doc.getString("category") ?: "",
                                imageUrl = doc.getString("imageUrl") ?: "",
                                isAvailable = doc.getBoolean("isAvailable") ?: true
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }.also { _products.value = it }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addProduct(
        name: String,
        description: String,
        price: Double,
        quantity: Int,
        category: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val vendorId = auth.currentUser?.uid ?: return@launch

                // Upload image if provided
                val imageUrl = imageUri?.let { uploadProductImage(it) } ?: ""

                val productData = hashMapOf(
                    "vendorId" to vendorId,
                    "name" to name,
                    "description" to description,
                    "price" to price,
                    "quantity" to quantity,
                    "category" to category,
                    "imageUrl" to imageUrl,
                    "isAvailable" to true,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )

                firestore.collection("products")
                    .add(productData)
                    .await()

                _successMessage.value = "Product added successfully!"
                loadProducts()

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add product"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProduct(
        productId: String,
        name: String,
        description: String,
        price: Double,
        quantity: Int,
        category: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val updates = mutableMapOf<String, Any>(
                    "name" to name,
                    "description" to description,
                    "price" to price,
                    "quantity" to quantity,
                    "category" to category,
                    "updatedAt" to FieldValue.serverTimestamp()
                )

                imageUri?.let {
                    updates["imageUrl"] = uploadProductImage(it)
                }

                firestore.collection("products")
                    .document(productId)
                    .update(updates)
                    .await()

                _successMessage.value = "Product updated successfully!"
                loadProducts()

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleProductAvailability(productId: String, isAvailable: Boolean) {
        viewModelScope.launch {
            try {
                firestore.collection("products")
                    .document(productId)
                    .update("isAvailable", isAvailable)
                    .await()

                loadProducts()
            } catch (e: Exception) {
                _error.value = "Failed to update availability"
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            try {
                firestore.collection("products")
                    .document(productId)
                    .delete()
                    .await()

                _successMessage.value = "Product deleted"
                loadProducts()
            } catch (e: Exception) {
                _error.value = "Failed to delete product"
            }
        }
    }

    private suspend fun uploadProductImage(uri: Uri): String {
        return try {
            val vendorId = auth.currentUser?.uid ?: ""
            val filename = "products/${vendorId}/${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child(filename)

            val uploadTask = storageRef.putFile(uri)

            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
                _uploadProgress.value = progress
            }

            uploadTask.await()
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

data class VendorProduct(
    val productId: String,
    val name: String,
    val description: String,
    val price: Double,
    val quantity: Int,
    val category: String,
    val imageUrl: String,
    val isAvailable: Boolean
)