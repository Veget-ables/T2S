package com.tsuchinoko.t2s.feature.schedule

import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountUiState

internal data class ScheduleGenUiState(
    val calendarAccountUiState: CalendarAccountUiState,
    val generatedEventsUiState: GeneratedEventsUiState,
) {
    companion object {
        val Empty = ScheduleGenUiState(
            calendarAccountUiState = CalendarAccountUiState.Initial,
            generatedEventsUiState = GeneratedEventsUiState.Empty,
        )
    }
}
