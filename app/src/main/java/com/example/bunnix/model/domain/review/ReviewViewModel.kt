package com.example.bunnix.model.domain.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.model.domain.model.Review
import com.example.bunnix.model.domain.uiState.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.onSuccess

class ReviewViewModel(private val addReviewUseCase: AddReviewUseCase) : ViewModel() {
    val state = MutableStateFlow<UiState<Review>>(UiState.Idle)

    fun onSubmitReview(orderId: String, rating: Int, comment: String) = viewModelScope.launch {
        state.value = UiState.Loading
        addReviewUseCase.execute(orderId, rating, comment)
            .onSuccess { state.value = UiState.Success(it) }
            .onFailure { state.value = UiState.Error(it.message ?: "Review failed") }
    }
}
