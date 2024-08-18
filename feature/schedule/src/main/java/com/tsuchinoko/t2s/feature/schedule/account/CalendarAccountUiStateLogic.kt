package com.tsuchinoko.t2s.feature.schedule.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsuchinoko.t2s.core.data.CalendarRepository
import com.tsuchinoko.t2s.core.domain.GetAccountCalendarsUseCase
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.model.Calendar
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CalendarAccountUiStateLogicModule {
    @Singleton
    @Binds
    internal abstract fun bindCalendarAccountUiStateLogic(impl: CalendarAccountUiStateLogicImpl): CalendarAccountUiStateLogic
}

internal interface CalendarAccountUiStateLogic {
    val calendarAccountUiState: StateFlow<CalendarAccountUiState>

    context(ViewModel)
    fun fetchCalendars(
        account: Account,
    )

    context(ViewModel)
    fun updateTargetCalendar(
        calendar: Calendar,
    )
}

internal class CalendarAccountUiStateLogicImpl @Inject constructor(
    private val calendarRepository: CalendarRepository,
    private val getAccountCalendarsUseCase: GetAccountCalendarsUseCase,
) : CalendarAccountUiStateLogic {
    private val _calendarAccountUiState: MutableStateFlow<CalendarAccountUiState> =
        MutableStateFlow(CalendarAccountUiState.Initial)
    override val calendarAccountUiState: StateFlow<CalendarAccountUiState> =
        _calendarAccountUiState.asStateFlow()

    context(ViewModel)
    override fun fetchCalendars(account: Account) {
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

    context(ViewModel)
    override fun updateTargetCalendar(calendar: Calendar) {
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
