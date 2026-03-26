package com.example.bunnix.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.database.models.Product
import com.example.bunnix.database.supabase.storage.ProductStorage
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
    private val storage: FirebaseStorage, // Kept if you use it elsewhere, otherwise can be removed
    @dagger.hilt.android.qualifiers.ApplicationContext
    private val context: android.content.Context
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

                // 1. Get vendor name
                val vendorDoc = firestore.collection("vendorProfiles")
                    .document(vendorId)
                    .get()
                    .await()
                val vendorName = vendorDoc.getString("businessName") ?: "Unknown Vendor"

                // 2. Generate a Firestore Document Reference FIRST to get the real ID
                val docRef = firestore.collection("products").document()
                val productId = docRef.id

                // 3. Upload Image to Supabase using the Firestore ID
                // WARNING: Ensure ProductStorage ONLY uploads. If it inserts to DB, that causes your RLS error.
                val imageUrl = ProductStorage.uploadProductImage(
                    context = context,
                    productId = productId, // Use the real Firestore ID for consistency
                    imageUri = imageUri,
                    imageIndex = 0
                ).getOrThrow()

                // 4. Create the Product object
                val product = Product(
                    productId = productId, // Use the ID we generated
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

                // 5. Save to Firebase Firestore (Set the document using the ID we generated)
                docRef.set(product).await()

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

                if (imageUri != null) {
                    // Upload new image if provided
                    val imageUrl = ProductStorage.uploadProductImage(
                        context = context,
                        productId = productId,
                        imageUri = imageUri,
                        imageIndex = 0
                    ).getOrThrow()
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

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}