package com.tsuchinoko.t2s.feature.schedule.gen

internal data class ScheduleGenUiState(
    val prompt: String,
    val generatedEventsUiState: GeneratedEventsUiState,
    val displayType: DisplayType,
) {
    companion object {
        val Initial = ScheduleGenUiState(
            prompt = "",
            generatedEventsUiState = GeneratedEventsUiState.Loading,
            displayType = DisplayType.Carousel,
        )
    }
}

internal sealed interface RegistryResultUiState {
    data object Standby : RegistryResultUiState
    data object Loading : RegistryResultUiState
    data object Success : RegistryResultUiState
    data class Error(val message: String) : RegistryResultUiState
}

enum class DisplayType {
    List,
    Carousel,
}
