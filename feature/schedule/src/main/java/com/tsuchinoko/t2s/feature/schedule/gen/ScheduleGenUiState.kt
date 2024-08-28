package com.tsuchinoko.t2s.feature.schedule.gen

internal data class ScheduleGenUiState(
    val prompt: String,
    val generatedEventsUiState: GeneratedEventsUiState,
    val eventsRegistryUiState: EventsRegistryUiState?,
) {
    companion object {
        val Initial = ScheduleGenUiState(
            prompt = "",
            generatedEventsUiState = GeneratedEventsUiState.Loading,
            eventsRegistryUiState = null,
        )
    }
}

internal sealed interface EventsRegistryUiState {
    data object Loading : EventsRegistryUiState
    data object Success : EventsRegistryUiState
    data class Error(val message: String) : EventsRegistryUiState
}
