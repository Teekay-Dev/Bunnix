package com.example.bunnix.presentation.viewmodel


import androidx.lifecycle.ViewModel
import com.example.bunnix.database.models.Service
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ServiceViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services: StateFlow<List<Service>> = _services

    init {
        loadServices()
    }

    private fun loadServices() {

        db.collection("services")
            .addSnapshotListener { snapshot, _ ->

                if (snapshot != null) {

                    val serviceList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Service::class.java)
                    }

                    _services.value = serviceList
                }
            }
    }
}