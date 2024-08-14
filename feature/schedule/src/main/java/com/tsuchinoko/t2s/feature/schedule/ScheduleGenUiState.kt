package com.tsuchinoko.t2s.feature.schedule

internal data class ScheduleGenUiState(
    val calendarUiState: CalendarUiState,
    val generatedEventsUiState: GeneratedEventsUiState
) {
    companion object {
        val Empty = ScheduleGenUiState(
            calendarUiState = CalendarUiState.Initial,
            generatedEventsUiState = GeneratedEventsUiState.Empty
        )
    }
}
