package com.example.bunnix.domain.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.domain.uiState.UiState
import com.example.bunnix.model.domain.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val addProductUseCase: AddProductUseCase
) : ViewModel() {

    val state = MutableStateFlow<UiState<Product>>(UiState.Idle)

    fun onAddProduct(name: String, price: Double) = viewModelScope.launch {
        state.value = UiState.Loading
        addProductUseCase.execute(name, price)
            .onSuccess { state.value = UiState.Success(it) }
            .onFailure { state.value = UiState.Error(it.message ?: "Add failed") }
    }
}

