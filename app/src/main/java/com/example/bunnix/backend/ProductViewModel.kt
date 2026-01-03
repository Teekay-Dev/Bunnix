package com.example.bunnix.backend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _vendorId = MutableStateFlow<Int?>(null)

    // Vendor products (auto-updates when DB changes)
    val vendorProducts: StateFlow<List<Product>> =
        _vendorId
            .filterNotNull()
            .flatMapLatest { vendorId ->
                repository.getProductsByVendor(vendorId)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    fun setVendor(vendorId: Int) {
        _vendorId.value = vendorId
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            repository.addProduct(product)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.update(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.delete(product)
        }
    }
}
