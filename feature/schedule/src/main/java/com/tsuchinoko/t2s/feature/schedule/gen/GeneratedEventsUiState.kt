package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.compose.runtime.Immutable
import com.tsuchinoko.t2s.core.model.ScheduleEvent

@Immutable
internal sealed interface GeneratedEventsUiState {
    data object Loading : GeneratedEventsUiState
    data class Generated(val events: List<ScheduleEvent>) : GeneratedEventsUiState
    data class Error(val message: String) : GeneratedEventsUiState
}

internal val List<ScheduleEvent>.copiedText: String
    get() = map { event -> event.copiedText }.joinToString(separator = "") { it + "\n\n" }

internal val ScheduleEvent.copiedText: String
    get() = "${displayDateTime.value}\n$title\n$memo"
