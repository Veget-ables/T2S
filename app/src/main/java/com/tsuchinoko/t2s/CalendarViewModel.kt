package com.tsuchinoko.t2s

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor() : ViewModel() {

    private val _calendarUiState: MutableStateFlow<CalendarUiState> =
        MutableStateFlow(CalendarUiState.Initial)
    val calendarUiState: StateFlow<CalendarUiState> =
        _calendarUiState.asStateFlow()


    fun fetchCalendars(accountName: String) {

    }
}