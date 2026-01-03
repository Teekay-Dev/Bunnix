package com.example.bunnix.backend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VendorProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _vendorProducts = MutableStateFlow<List<Product>>(emptyList())
    val vendorProducts: StateFlow<List<Product>> = _vendorProducts

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun loadProducts(vendorId: Int) {
        viewModelScope.launch {
            _loading.value = true
            repository.getProductsByVendor(vendorId).collect {
                _vendorProducts.value = it
                _loading.value = false
            }
        }
    }

    fun saveProduct(product: Product) {
        viewModelScope.launch {
            repository.addProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.delete(product)
        }
    }
}
