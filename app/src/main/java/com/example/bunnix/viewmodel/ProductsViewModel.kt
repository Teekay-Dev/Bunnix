package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.data.auth.getErrorMessage
import com.example.bunnix.data.auth.getOrNull
import com.example.bunnix.data.auth.isSuccess
import com.example.bunnix.database.models.Product
import com.example.bunnix.domain.usecase.product.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductsUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val addProductUseCase: AddProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val getVendorProductsUseCase: GetVendorProductsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState: StateFlow<ProductsUiState> = _uiState

    private var currentVendorId: String = ""

    fun loadProducts(vendorId: String) {
        currentVendorId = vendorId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = getVendorProductsUseCase(vendorId)
            when {
                result.isSuccess() -> _uiState.update {
                    it.copy(isLoading = false, products = result.getOrNull() ?: emptyList())
                }
                else -> _uiState.update {
                    it.copy(isLoading = false, error = result.getErrorMessage())
                }
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            val result = addProductUseCase(
                vendorId = product.vendorId,
                vendorName = product.vendorName,
                name = product.name,
                description = product.description,
                price = product.price,
                category = product.category,
                imageUrls = product.imageUrls,
                totalStock = product.totalStock,
                discountPrice = product.discountPrice,
                variants = product.variants,
                tags = product.tags
            )
            when {
                result.isSuccess() -> loadProducts(currentVendorId)
                else -> _uiState.update { it.copy(error = result.getErrorMessage()) }
            }
        }
    }

    fun updateProduct(productId: String, updates: Map<String, Any>) {
        viewModelScope.launch {
            val result = updateProductUseCase(productId, updates)
            when {
                result.isSuccess() -> loadProducts(currentVendorId)
                else -> _uiState.update { it.copy(error = result.getErrorMessage()) }
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            val result = deleteProductUseCase(productId)
            when {
                result.isSuccess() -> loadProducts(currentVendorId)
                else -> _uiState.update { it.copy(error = result.getErrorMessage()) }
            }
        }
    }
}