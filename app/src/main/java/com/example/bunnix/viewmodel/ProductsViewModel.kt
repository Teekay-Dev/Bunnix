package com.example.bunnix.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.database.models.Product
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
class ProductsViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

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

                        val productsList = snapshot?.toObjects(Product::class.java) ?: emptyList()
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

                // Get vendor name from Firestore
                val vendorDoc = firestore.collection("vendorProfiles")
                    .document(vendorId)
                    .get()
                    .await()
                val vendorName = vendorDoc.getString("businessName") ?: "Unknown Vendor"



                // Upload image to Firebase Storage
                val imageUrl = uploadProductImage(imageUri, vendorId)

                // Create product using the proper Product model
                val product = Product(
                    productId = "", // Firestore will auto-generate
                    vendorId = vendorId,
                    vendorName = vendorName,
                    name = name,
                    description = description,
                    price = price,
                    discountPrice = null,
                    category = category,
                    imageUrls = listOf(imageUrl),
                    variants = emptyList(),
                    totalStock = quantity,
                    inStock = quantity > 0,
                    tags = listOf(category.lowercase()),
                    views = 0,
                    sold = 0,
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )

                val docRef = firestore.collection("products")
                    .add(product)
                    .await()

                docRef.update("productId", docRef.id).await()

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
                    "totalStock" to quantity,
                    "inStock" to (quantity > 0),
                    "category" to category,
                    "updatedAt" to Timestamp.now()
                )

                // Upload new image if provided
                if (imageUri != null) {
                    val imageUrl = uploadProductImage(imageUri, vendorId)
                    updateData["imageUrls"] = listOf(imageUrl)
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
                    .update("inStock", isAvailable)
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