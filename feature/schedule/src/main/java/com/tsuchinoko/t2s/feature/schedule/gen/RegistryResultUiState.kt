package com.tsuchinoko.t2s.feature.schedule.gen

internal sealed interface RegistryResultUiState {
    data object Standby : RegistryResultUiState
    data object Loading : RegistryResultUiState
    data object Success : RegistryResultUiState
    data class Error(val message: String) : RegistryResultUiState
}
