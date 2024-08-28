package com.tsuchinoko.t2s.feature.schedule.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsuchinok.t2s.core.common.error.RecoverableIntentError
import com.tsuchinoko.t2s.core.data.AccountRepository
import com.tsuchinoko.t2s.core.data.CalendarRepository
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.ui.Result
import com.tsuchinoko.t2s.core.ui.resultFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CalendarAccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val calendarRepository: CalendarRepository,
) : ViewModel() {
    private val _needAccount: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val needAccount: StateFlow<Boolean> = _needAccount.asStateFlow()

    private val fetchAccountCalendarsResult = accountRepository
        .getAccount()
        .flatMapLatest { account ->
            resultFlow {
                if (account == null) return@resultFlow
                calendarRepository.fetchCalendars(account)
            }
        }

    internal val calendarAccountUiState: StateFlow<CalendarAccountUiState> =
        combine(
            accountRepository.getAccount(),
            calendarRepository.getAccountCalendars(),
            calendarRepository.getTargetCalendarId(),
            fetchAccountCalendarsResult,
        ) { account, calendars, calendarId, result ->
            when {
                result is Result.Loading -> {
                    CalendarAccountUiState.Loading
                }
                result is Result.Error -> {
                    when (val exception = result.exception) {
                        is RecoverableIntentError -> {
                            CalendarAccountUiState.RecoverableIntentError(exception.intent)
                        }

                        else -> {
                            CalendarAccountUiState.Error(exception.message ?: "")
                        }
                    }
                }
                account != null && calendars.isNotEmpty() -> {
                    val targetCalendar =
                        if (calendarId == null) calendars[0] else calendars.first { it.id == calendarId }
                    CalendarAccountUiState.AccountSelected(
                        account = account,
                        calendars = calendars,
                        targetCalendar = targetCalendar,
                    )
                }
                else -> {
                    CalendarAccountUiState.Initial
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            CalendarAccountUiState.Initial,
        )

    init {
        viewModelScope.launch {
            _needAccount.value = accountRepository
                .getAccount()
                .map { account ->
                    account == null
                }
                .first()
        }
    }

    fun updateAccount(account: Account) {
        viewModelScope.launch {
            accountRepository.setAccount(account)
        }
    }

    fun updateTargetCalendar(calendar: Calendar) {
        viewModelScope.launch {
            calendarRepository.setTargetCalendar(calendar)
        }
    }
}
