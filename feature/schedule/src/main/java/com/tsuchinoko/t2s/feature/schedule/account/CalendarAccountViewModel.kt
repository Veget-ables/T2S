package com.tsuchinoko.t2s.feature.schedule.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsuchinok.t2s.core.common.resultFlow
import com.tsuchinoko.t2s.core.data.AccountRepository
import com.tsuchinoko.t2s.core.data.CalendarRepository
import com.tsuchinoko.t2s.core.domain.FetchAccountCalendarsUseCase
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.ui.ObservableLoadingCounter
import com.tsuchinoko.t2s.core.ui.collectResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CalendarAccountViewModel @Inject constructor(
    accountRepository: AccountRepository,
    private val calendarRepository: CalendarRepository,
    private val fetchAccountCalendarsUseCase: FetchAccountCalendarsUseCase,
) : ViewModel() {
    private val calendarLoadingState = ObservableLoadingCounter()

    val calendarAccountUiState: StateFlow<CalendarAccountUiState> =
        combine(
            accountRepository.getAccount(),
            calendarRepository.getAccountCalendars(),
            calendarRepository.getTargetCalendarId(),
            calendarLoadingState.observable,
        ) { account, calendars, calendarId, isCalendarLoading ->
            if (isCalendarLoading) {
                CalendarAccountUiState.Loading
            } else if (account != null && calendars.isNotEmpty()) {
                val targetCalendar =
                    if (calendarId == null) calendars[0] else calendars.first { it.id == calendarId }
                CalendarAccountUiState.AccountSelected(
                    account = account,
                    calendars = calendars,
                    targetCalendar = targetCalendar,
                )
            } else {
                CalendarAccountUiState.Initial
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            CalendarAccountUiState.Initial,
        )

    fun fetchCalendars(account: Account) {
        viewModelScope.launch {
            resultFlow {
                fetchAccountCalendarsUseCase(account)
            }.collectResult(
                counter = calendarLoadingState,
                onError = {
                    // TODO
                },
            )
        }
    }

    fun updateTargetCalendar(calendar: Calendar) {
        viewModelScope.launch {
            calendarRepository.setTargetCalendar(calendar)
        }
    }
}
