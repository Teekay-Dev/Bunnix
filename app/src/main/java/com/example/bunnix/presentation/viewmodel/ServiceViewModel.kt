package com.example.bunnix.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.database.models.Service
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services.asStateFlow()

    // ✅ ADD THESE
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _featuredServices = MutableStateFlow<List<Service>>(emptyList())
    val featuredServices: StateFlow<List<Service>> = _featuredServices.asStateFlow()

    init {
        loadServices()
        loadFeaturedServices()
    }


    fun loadFeaturedServices() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val snapshot = firestore.collection("services")
                    .limit(10)
                    .get()
                    .await()
                _featuredServices.value = snapshot.toObjects(Service::class.java)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load featured services"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadServices() {
        firestore.collection("services")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    _services.value = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Service::class.java)
                    }
                }
            }
    }

    fun getService(serviceId: String): Flow<Service?> = flow {
        try {
            val snapshot = firestore.collection("services")
                .document(serviceId)
                .get()
                .await()

            val service = snapshot.toObject(Service::class.java)
            emit(service)
        } catch (e: Exception) {
            emit(null)
        }
    }

    // ✅ FIXED - Now has _isLoading and _error
    fun getServicesByVendor(vendorId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val snapshot = firestore.collection("services")
                    .whereEqualTo("vendorId", vendorId)
                    .get()
                    .await()

                val serviceList = snapshot.toObjects(Service::class.java)
                _services.value = serviceList

            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load vendor services"
            } finally {
                _isLoading.value = false
            }
        }
    }
}