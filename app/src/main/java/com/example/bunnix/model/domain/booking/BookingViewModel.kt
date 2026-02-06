package com.example.bunnix.model.domain.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.model.domain.uiState.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class BookingViewModel(
    private val createBookingUseCase: CreateBookingUseCase
) : ViewModel() {

    val state = MutableStateFlow<UiState<Booking>>(UiState.Idle)

    fun onBookService(serviceId: String, vendorId: String, scheduledTime: Instant) = viewModelScope.launch {
        state.value = UiState.Loading
        createBookingUseCase.execute(serviceId, vendorId, scheduledTime)
            .onSuccess { state.value = UiState.Success(it) }
            .onFailure { state.value = UiState.Error(it.message ?: "Booking failed") }
    }
}
