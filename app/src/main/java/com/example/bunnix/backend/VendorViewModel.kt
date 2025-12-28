package com.example.bunnix.backend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VendorViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _vendorProducts = MutableStateFlow<List<Product>>(emptyList())
    val vendorProducts: StateFlow<List<Product>> = _vendorProducts.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun loadProducts(vendorId: Int) {
        _loading.value = true
        viewModelScope.launch {
            repository.getProductsByVendor(vendorId)
                .collect { products ->
                    _vendorProducts.value = products
                    _loading.value = false
                }
        }
    }

    fun addProduct(product: Product, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addProduct(product)
            onComplete()
        }
    }

    fun updateProduct(product: Product, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.update(product)
            onComplete()
        }
    }

    fun deleteProduct(product: Product, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.delete(product)
            onComplete()
        }
    }
}
