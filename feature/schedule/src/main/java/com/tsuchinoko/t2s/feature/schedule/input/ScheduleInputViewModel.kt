package com.tsuchinoko.t2s.feature.schedule.input

import androidx.lifecycle.ViewModel
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
internal class ScheduleInputViewModel @Inject constructor() : ViewModel() {

    private val _calendarAccountUiState: MutableStateFlow<CalendarAccountUiState> =
        MutableStateFlow(CalendarAccountUiState.Initial)
    val calendarAccountUiState: StateFlow<CalendarAccountUiState> = _calendarAccountUiState.asStateFlow()
}
