package com.tsuchinoko.t2s

import android.accounts.Account

sealed interface CalendarUiState {

    object Initial : CalendarUiState

    object Loading : CalendarUiState

    data class Success(val account: Account, val calendarList: List<String>) : CalendarUiState

    data class Error(val errorMessage: String) : CalendarUiState
}