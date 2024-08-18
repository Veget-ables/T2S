package com.tsuchinoko.t2s.feature.schedule.gen

internal data class ScheduleGenUiState(
    val generatedEventsUiState: GeneratedEventsUiState,
) {
    companion object {
        val Initial = ScheduleGenUiState(
            generatedEventsUiState = GeneratedEventsUiState.Loading,
        )
    }
}
