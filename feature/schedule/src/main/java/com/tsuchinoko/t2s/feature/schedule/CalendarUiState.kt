package com.tsuchinoko.t2s.feature.schedule

import com.tsuchinoko.t2s.feature.schedule.model.Calendar

sealed interface CalendarUiState {

    object Initial : CalendarUiState

    object Loading : CalendarUiState

    data class Success(val accountName: String, val calendars: List<Calendar>) : CalendarUiState

    data class Error(val errorMessage: String) : CalendarUiState
}