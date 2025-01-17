package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.compose.runtime.Immutable

@Immutable
internal data class ScheduleGenUiState(
    val prompt: String,
    val generatedEventsUiState: GeneratedEventsUiState,
) {
    companion object {
        val Initial = ScheduleGenUiState(
            prompt = "",
            generatedEventsUiState = GeneratedEventsUiState.Loading,
        )
    }
}
