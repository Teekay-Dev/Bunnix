package com.example.bunnix.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.database.models.Product
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * ViewModel for Products
 * Fetches products from Firestore in real-time
 */
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // Load products when ViewModel is created
        loadProducts()
    }

    /**
     * Load all products from Firestore
     */
    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // ✅ Fetch from Firestore /products collection
                val snapshot = firestore.collection("products")
                    .whereEqualTo("inStock", true)  // Only show in-stock products
                    .get()
                    .await()

                val productList = snapshot.toObjects(Product::class.java)
                _products.value = productList

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load products"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Get products by category
     */
    fun getProductsByCategory(category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val snapshot = firestore.collection("products")
                    .whereEqualTo("category", category)
                    .whereEqualTo("inStock", true)
                    .get()
                    .await()

                val productList = snapshot.toObjects(Product::class.java)
                _products.value = productList

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load products"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Get products by vendor
     */
    fun getProductsByVendor(vendorId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val snapshot = firestore.collection("products")
                    .whereEqualTo("vendorId", vendorId)
                    .get()
                    .await()

                val productList = snapshot.toObjects(Product::class.java)
                _products.value = productList

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load vendor products"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Search products by name
     */
    fun searchProducts(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Get all products first (Firestore doesn't support text search natively)
                val snapshot = firestore.collection("products")
                    .whereEqualTo("inStock", true)
                    .get()
                    .await()

                val allProducts = snapshot.toObjects(Product::class.java)
                
                // Filter locally
                val filtered = allProducts.filter { product ->
                    product.name.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true) ||
                    product.tags.any { it.contains(query, ignoreCase = true) }
                }

                _products.value = filtered

            } catch (e: Exception) {
                _error.value = e.message ?: "Search failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Get single product by ID
     */
    suspend fun getProductById(productId: String): Product? {
        return try {
            val snapshot = firestore.collection("products")
                .document(productId)
                .get()
                .await()

            snapshot.toObject(Product::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Refresh products
     */
    fun refresh() {
        loadProducts()
    }
}
