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

data class VendorProduct(
    val productId: String = "",
    val vendorId: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val category: String = "",
    val imageUrl: String = "",
    val isAvailable: Boolean = true,
    val createdAt: Long = 0L
)

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

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun loadProducts() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val vendorId = auth.currentUser?.uid ?: return@launch

                firestore.collection("products")
                    .whereEqualTo("vendorId", vendorId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            _error.value = error.message
                            return@addSnapshotListener
                        }

                        val productsList = snapshot?.documents?.mapNotNull { doc ->
                            try {
                                VendorProduct(
                                    productId = doc.id,
                                    vendorId = doc.getString("vendorId") ?: "",
                                    name = doc.getString("name") ?: "",
                                    description = doc.getString("description") ?: "",
                                    price = doc.getDouble("price") ?: 0.0,
                                    quantity = doc.getLong("quantity")?.toInt() ?: 0,
                                    category = doc.getString("category") ?: "",
                                    imageUrl = doc.getString("imageUrl") ?: "",
                                    isAvailable = doc.getBoolean("isAvailable") ?: true,
                                    createdAt = doc.getLong("createdAt") ?: 0L
                                )
                            } catch (e: Exception) {
                                null
                            }
                        } ?: emptyList()

                        _products.value = productsList
                        _isLoading.value = false
                    }

            } catch (e: Exception) {
                _error.value = e.message
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
        imageUri: Uri
    ) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _uploadProgress.value = 0f
                _error.value = null

                val vendorId = auth.currentUser?.uid ?: return@launch

                // Upload image to Firebase Storage
                val imageUrl = uploadProductImage(imageUri, vendorId)

                // Create product document
                val productData = hashMapOf(
                    "vendorId" to vendorId,
                    "name" to name,
                    "description" to description,
                    "price" to price,
                    "quantity" to quantity,
                    "category" to category,
                    "imageUrl" to imageUrl,
                    "isAvailable" to true,
                    "createdAt" to System.currentTimeMillis(),
                    "updatedAt" to System.currentTimeMillis()
                )

                firestore.collection("products")
                    .add(productData)
                    .await()

                _successMessage.value = "Product added successfully"

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add product"
            } finally {
                _isLoading.value = false
                _uploadProgress.value = 0f
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
                    "quantity" to quantity,
                    "category" to category,
                    "updatedAt" to System.currentTimeMillis()
                )

                // Upload new image if provided
                if (imageUri != null) {
                    val imageUrl = uploadProductImage(imageUri, vendorId)
                    updateData["imageUrl"] = imageUrl
                }

                firestore.collection("products")
                    .document(productId)
                    .update(updateData)
                    .await()

                _successMessage.value = "Product updated successfully"

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update product"
            } finally {
                _isLoading.value = false
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

                _successMessage.value = "Product deleted successfully"

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete product"
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

            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    private suspend fun uploadProductImage(imageUri: Uri, vendorId: String): String {
        val storageRef = storage.reference
            .child("products/$vendorId/product_${System.currentTimeMillis()}.jpg")

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