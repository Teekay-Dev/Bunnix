package com.example.bunnix.domain.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.domain.uiState.UiState
import com.example.bunnix.model.domain.model.Service
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ServiceViewModel(
    private val addServiceUseCase: AddServiceUseCase
) : ViewModel() {

    val state = MutableStateFlow<UiState<Service>>(UiState.Idle)

    fun onAddService(title: String, price: Double, duration: Int) = viewModelScope.launch {
        state.value = UiState.Loading
        addServiceUseCase.execute(title, price, duration)
            .onSuccess { state.value = UiState.Success(it) }
            .onFailure { state.value = UiState.Error(it.message ?: "Add failed") }
    }
}
