package com.example.bunnix.backend


import androidx.compose.ui.unit.IntRect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.Int
import kotlin.String

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {
    val products = repository.allProducts.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addProduct(imageUri: String, name: String, description: String, price: String, quantity: String) {
        viewModelScope.launch {
            repository.addProduct(
                Product(
                    image_url = imageUri,
                    name = name,
                    description = description,
                    price = price,
                    quantity = quantity
                )
            )
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            repository.update(product)
        }
    }


    fun delete(product: Product) {
        viewModelScope.launch { repository.delete(product) }
    }

    fun clearAll() {
        viewModelScope.launch { repository.clearAll() }
    }
}
