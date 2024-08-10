package com.tsuchinoko.t2s.feature.schedule

sealed interface ScheduleGenUiState {

    object Initial : ScheduleGenUiState

    object Loading : ScheduleGenUiState

    data class Success(val scheduleEvents: List<ScheduleEvent>) : ScheduleGenUiState

    data class Error(val errorMessage: String) : ScheduleGenUiState
}
