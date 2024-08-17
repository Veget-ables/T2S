package com.tsuchinoko.t2s.feature.schedule.account

import com.tsuchinoko.t2s.core.model.Calendar

internal sealed interface CalendarUiState {
    data object Initial : CalendarUiState
    data object Loading : CalendarUiState

    data class AccountSelected(
        val accountName: String,
        val calendars: List<Calendar>,
        val targetCalendar: Calendar,
    ) : CalendarUiState

    data class Error(val message: String) : CalendarUiState
}
