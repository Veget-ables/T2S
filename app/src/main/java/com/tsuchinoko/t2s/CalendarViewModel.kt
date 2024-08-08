package com.tsuchinoko.t2s

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tsuchinoko.t2s.data.AccountRepository
import com.tsuchinoko.t2s.domain.GetAccountCalendarsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    accountRepository: AccountRepository,
    private val getAccountCalendarsUseCase: GetAccountCalendarsUseCase
) : ViewModel() {

    private val _calendarUiState: MutableStateFlow<CalendarUiState> =
        MutableStateFlow(CalendarUiState.Initial)
    val calendarUiState: StateFlow<CalendarUiState> =
        _calendarUiState.asStateFlow()

    init {
        accountRepository.getAccount()
    }

    fun fetchCalendars(accountName: String) {
        viewModelScope.launch {
            val calendars = getAccountCalendarsUseCase(accountName)
            _calendarUiState.value = CalendarUiState.Success(
                accountName = accountName,
                calendars = calendars
            )
        }
    }
}