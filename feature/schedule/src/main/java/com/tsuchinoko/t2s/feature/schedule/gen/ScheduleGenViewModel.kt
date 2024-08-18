package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsuchinoko.t2s.core.data.CalendarRepository
import com.tsuchinoko.t2s.core.data.ScheduleGenRepository
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountUiState
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountUiStateLogic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ScheduleGenViewModel @Inject constructor(
    calendarAccountUiStateLogic: CalendarAccountUiStateLogic,
    private val calendarRepository: CalendarRepository,
    private val scheduleGenRepository: ScheduleGenRepository,
) : ViewModel(),
    CalendarAccountUiStateLogic by calendarAccountUiStateLogic {

    private val _scheduleGenUiState: MutableStateFlow<ScheduleGenUiState> =
        MutableStateFlow(ScheduleGenUiState.Initial)
    val scheduleGenUiState: StateFlow<ScheduleGenUiState> = _scheduleGenUiState.asStateFlow()

    fun updateInputEvent(event: ScheduleEvent) {
        val uiState = _scheduleGenUiState.value.generatedEventsUiState
        if (uiState is GeneratedEventsUiState.Generated) {
            val newEvents = uiState.events.toTypedArray().apply {
                val targetIndex = indexOfFirst { it.id == event.id }
                this[targetIndex] = event
            }.toList()
            _scheduleGenUiState.update {
                it.copy(generatedEventsUiState = GeneratedEventsUiState.Generated(newEvents))
            }
        }
    }

    fun sendPrompt(prompt: String) {
        _scheduleGenUiState.update {
            it.copy(generatedEventsUiState = GeneratedEventsUiState.Loading)
        }

        viewModelScope.launch {
            try {
                val scheduleEvents = scheduleGenRepository.generate(prompt)
                _scheduleGenUiState.update {
                    it.copy(generatedEventsUiState = GeneratedEventsUiState.Generated(scheduleEvents))
                }
            } catch (e: Exception) {
                _scheduleGenUiState.update {
                    it.copy(
                        generatedEventsUiState = GeneratedEventsUiState.Error(
                            e.localizedMessage ?: "",
                        ),
                    )
                }
            }
        }
    }

    fun registryEvents() {
        val calendarUiState = calendarAccountUiState.value
        val generatedEventsUiState = scheduleGenUiState.value.generatedEventsUiState
        if (calendarUiState is CalendarAccountUiState.AccountSelected && generatedEventsUiState is GeneratedEventsUiState.Generated) {
            viewModelScope.launch {
                calendarRepository.registryEvents(
                    calendarId = calendarUiState.targetCalendar.id,
                    events = generatedEventsUiState.events,
                )
            }
        }
    }
}
