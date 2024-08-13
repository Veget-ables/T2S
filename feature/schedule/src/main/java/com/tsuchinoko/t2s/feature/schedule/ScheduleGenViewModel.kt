package com.tsuchinoko.t2s.feature.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsuchinoko.t2s.core.data.CalendarRepository
import com.tsuchinoko.t2s.core.data.ScheduleGenRepository
import com.tsuchinoko.t2s.core.domain.GetAccountCalendarsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleGenViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val getAccountCalendarsUseCase: GetAccountCalendarsUseCase,
    private val scheduleGenRepository: ScheduleGenRepository
) : ViewModel() {
    private val _calendarUiState: MutableStateFlow<CalendarUiState> =
        MutableStateFlow(CalendarUiState.Initial)
    val calendarUiState: StateFlow<CalendarUiState> =
        _calendarUiState.asStateFlow()

    private val _scheduleGenUiState: MutableStateFlow<ScheduleGenUiState> =
        MutableStateFlow(ScheduleGenUiState.Initial)
    val scheduleGenUiState: StateFlow<ScheduleGenUiState> =
        this._scheduleGenUiState.asStateFlow()

    fun fetchCalendars(accountName: String) {
        viewModelScope.launch {
            val calendars = getAccountCalendarsUseCase(accountName)
            _calendarUiState.value = CalendarUiState.Success(
                accountName = accountName,
                calendars = calendars
            )
        }
    }

    fun sendPrompt(prompt: String) {
        _scheduleGenUiState.value = ScheduleGenUiState.Loading

        viewModelScope.launch {
            try {
                val scheduleEvents = scheduleGenRepository.generate(prompt)
                _scheduleGenUiState.value = ScheduleGenUiState.Success(scheduleEvents)
            } catch (e: Exception) {
                _scheduleGenUiState.value = ScheduleGenUiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    fun registryEvents() {
        viewModelScope.launch {
            calendarRepository.registryEvents(emptyList())
        }
    }
}
