package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.tsuchinoko.t2s.core.data.CalendarRepository
import com.tsuchinoko.t2s.core.domain.SuggestScheduleEventsUseCase
import com.tsuchinoko.t2s.core.model.CalendarId
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import com.tsuchinoko.t2s.core.ui.Result
import com.tsuchinoko.t2s.core.ui.resultFlow
import com.tsuchinoko.t2s.feature.schedule.ScheduleGen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ScheduleGenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val calendarRepository: CalendarRepository,
    private val suggestScheduleEventsUseCase: SuggestScheduleEventsUseCase,
) : ViewModel() {
    private val _scheduleGenUiState: MutableStateFlow<ScheduleGenUiState> =
        MutableStateFlow(ScheduleGenUiState.Initial)
    val scheduleGenUiState: StateFlow<ScheduleGenUiState> = _scheduleGenUiState.asStateFlow()

    private val _registryResultUiState: MutableStateFlow<RegistryResultUiState> = MutableStateFlow(RegistryResultUiState.Standby)
    val registryResultUiState: StateFlow<RegistryResultUiState> =
        _registryResultUiState.asStateFlow()

    init {
        generateEvents()
    }

    fun generateEvents() {
        val prompt = savedStateHandle.toRoute<ScheduleGen>().prompt
        viewModelScope.launch {
            resultFlow {
                suggestScheduleEventsUseCase(prompt)
            }.collect { result ->
                val eventsUiState = when (result) {
                    Result.Loading -> GeneratedEventsUiState.Loading
                    is Result.Success -> GeneratedEventsUiState.Generated(result.data)
                    is Result.Error -> {
                        GeneratedEventsUiState.Error(result.exception.localizedMessage ?: "")
                    }
                }
                _scheduleGenUiState.update {
                    it.copy(
                        prompt = prompt,
                        generatedEventsUiState = eventsUiState,
                    )
                }
            }
        }
    }

    fun addEvent(event: ScheduleEvent) {
        val uiState = _scheduleGenUiState.value.generatedEventsUiState
        if (uiState is GeneratedEventsUiState.Generated) {
            val newEvents = uiState.events.toMutableList().apply { add(0, event) }
            _scheduleGenUiState.update {
                it.copy(generatedEventsUiState = GeneratedEventsUiState.Generated(newEvents))
            }
        }
    }

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

    fun deleteEvent(event: ScheduleEvent) {
        val uiState = _scheduleGenUiState.value.generatedEventsUiState
        if (uiState is GeneratedEventsUiState.Generated) {
            val newEvents = uiState.events.toMutableList().apply {
                remove(event)
            }
            _scheduleGenUiState.update {
                it.copy(generatedEventsUiState = GeneratedEventsUiState.Generated(newEvents))
            }
        }
    }

    fun registryEvents(calendarId: CalendarId, events: List<ScheduleEvent>) {
        viewModelScope.launch {
            resultFlow {
                calendarRepository.registryEvents(
                    calendarId = calendarId,
                    events = events,
                )
            }.collect { result ->
                val uiState = when (result) {
                    Result.Loading -> RegistryResultUiState.Loading
                    is Result.Success -> RegistryResultUiState.Success
                    is Result.Error -> {
                        RegistryResultUiState.Error(result.exception.localizedMessage ?: "")
                    }
                }
                _registryResultUiState.update { uiState }
            }
        }
    }
}
