package com.example.bunnix.backend

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.google.android.gms.maps.model.LatLng
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackingViewModel : ViewModel() {
    // This holds the live position of the delivery guy
    private val _deliveryLocation = mutableStateOf(LatLng(6.5244, 3.3792))
    val deliveryLocation: State<LatLng> = _deliveryLocation

    init {
        simulateMovement()
    }

    // This simulates the delivery guy moving toward the user
    private fun simulateMovement() {
        viewModelScope.launch {
            repeat(100) {
                delay(2000) // Update every 2 seconds
                val current = _deliveryLocation.value
                // Slightly adjust lat/lng to simulate driving
                _deliveryLocation.value = LatLng(current.latitude + 0.0001, current.longitude + 0.0001)
            }
        }
    }
}