package com.example.bunnix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.data.auth.AuthResult
import com.example.bunnix.database.models.Service
import com.example.bunnix.domain.usecase.service.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.toIntOrNull // Add this import

// ... rest of your code
data class ServicesUiState(
    val isLoading: Boolean = false,
    val services: List<Service> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val addServiceUseCase: AddServiceUseCase,
    private val updateServiceUseCase: UpdateServiceUseCase,
    private val deleteServiceUseCase: DeleteServiceUseCase,
    private val getVendorServicesUseCase: GetVendorServicesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServicesUiState())
    val uiState: StateFlow<ServicesUiState> = _uiState

    fun loadServices(vendorId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = getVendorServicesUseCase(vendorId)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, services = result.data)
                    }
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                is AuthResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun addService(service: Service) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = addServiceUseCase(
                name = service.name,
                description = service.description,
                price = service.price,
                duration = service.duration, // Pass directly, no conversion needed
                category = service.category,
                vendorId = service.vendorId,
                vendorName = service.vendorName // ADD THIS
            )) {
                is AuthResult.Success -> {
                    loadServices(service.vendorId)
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                is AuthResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun updateService(serviceId: String, updates: Map<String, Any>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = updateServiceUseCase(
                serviceId = serviceId,
                name = updates["name"] as? String,
                description = updates["description"] as? String,
                price = updates["price"] as? Double,
                duration = updates["duration"] as? Int, // Fix: Int? not String?
                category = updates["category"] as? String,
                imageUrl = updates["imageUrl"] as? String,
                availability = updates["availability"] as? List<String>,
                isActive = updates["isActive"] as? Boolean
            )) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    // Optionally reload services
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                is AuthResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun deleteService(serviceId: String, vendorId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = deleteServiceUseCase(serviceId)) {
                is AuthResult.Success -> {
                    loadServices(vendorId)
                }
                is AuthResult.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                is AuthResult.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}