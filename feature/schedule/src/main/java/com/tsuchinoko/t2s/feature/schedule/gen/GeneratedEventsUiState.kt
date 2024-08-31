package com.tsuchinoko.t2s.feature.schedule.gen

import com.tsuchinoko.t2s.core.model.ScheduleEvent

internal sealed interface GeneratedEventsUiState {
    data object Loading : GeneratedEventsUiState
    data class Generated(val events: List<ScheduleEvent>) : GeneratedEventsUiState
    data class Error(val message: String) : GeneratedEventsUiState
}
