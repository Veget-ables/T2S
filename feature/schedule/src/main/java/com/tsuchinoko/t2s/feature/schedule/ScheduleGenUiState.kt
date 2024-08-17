package com.tsuchinoko.t2s.feature.schedule

import com.tsuchinoko.t2s.feature.schedule.account.CalendarUiState

internal data class ScheduleGenUiState(
    val calendarUiState: CalendarUiState,
    val generatedEventsUiState: GeneratedEventsUiState,
) {
    companion object {
        val Empty = ScheduleGenUiState(
            calendarUiState = CalendarUiState.Initial,
            generatedEventsUiState = GeneratedEventsUiState.Empty,
        )
    }
}
