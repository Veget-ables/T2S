package com.tsuchinoko.t2s.feature.schedule.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsuchinoko.t2s.core.data.CalendarRepository
import com.tsuchinoko.t2s.core.domain.GetAccountCalendarsUseCase
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.model.Calendar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CalendarAccountGuideViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val getAccountCalendarsUseCase: GetAccountCalendarsUseCase,
) : ViewModel() {
    private val _calendarAccountUiState: MutableStateFlow<CalendarAccountUiState> =
        MutableStateFlow(CalendarAccountUiState.Initial)
    val calendarAccountUiState: StateFlow<CalendarAccountUiState> =
        _calendarAccountUiState.asStateFlow()

    fun fetchCalendars(account: Account) {
        viewModelScope.launch {
            val calendars = getAccountCalendarsUseCase(account)
            _calendarAccountUiState.update {
                CalendarAccountUiState.AccountSelected(
                    account = account,
                    calendars = calendars,
                    targetCalendar = calendars[0],
                )
            }
        }
    }

    fun updateTargetCalendar(calendar: Calendar) {
        val uiState = _calendarAccountUiState.value
        if (uiState is CalendarAccountUiState.AccountSelected) {
            viewModelScope.launch {
                calendarRepository.setTargetCalendar(calendar.id)
                _calendarAccountUiState.update {
                    uiState.copy(targetCalendar = calendar)
                }
            }
        }
    }
}
