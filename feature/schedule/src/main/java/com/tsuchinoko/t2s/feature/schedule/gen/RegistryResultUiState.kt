package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.compose.runtime.Immutable

@Immutable
internal sealed interface RegistryResultUiState {
    data object Standby : RegistryResultUiState
    data object Loading : RegistryResultUiState
    data object Success : RegistryResultUiState
    data object Error : RegistryResultUiState
}
