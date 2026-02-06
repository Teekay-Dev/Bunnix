package com.example.bunnix.model.domain.switchCase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bunnix.model.domain.uiState.UiState
import com.example.bunnix.model.domain.user.UserMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ModeViewModel(
    private val switchMode: SwitchModeUseCase
) : ViewModel() {

    val state = MutableStateFlow<UiState<UserMode>>(UiState.Idle)

    fun onToggleMode() = viewModelScope.launch {
        state.value = UiState.Loading
        switchMode.execute()
            .onSuccess { state.value = UiState.Success(it) }
            .onFailure { state.value = UiState.Error(it.message ?: "Mode switch failed") }
    }
}
