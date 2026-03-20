package com.example.bunnix.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.database.models.Service
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class ServiceViewModel @Inject constructor(
    private val firestore: FirebaseFirestore  // injected like ProductViewModel
) : ViewModel() {
    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services

    init {
        loadServices()
    }

    private fun loadServices() {
        firestore.collection("services")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    _services.value = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Service::class.java)
                    }
                }
            }
    }

    fun getService(serviceId: String): StateFlow<Service?> {
        return services.map { list ->
            list.find { it.serviceId == serviceId }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
    }

}