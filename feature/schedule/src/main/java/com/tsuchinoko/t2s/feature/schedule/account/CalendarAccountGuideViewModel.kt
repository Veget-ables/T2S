package com.tsuchinoko.t2s.feature.schedule.account

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class CalendarAccountGuideViewModel @Inject constructor(
    calendarAccountUiStateLogic: CalendarAccountUiStateLogic,
) : ViewModel(),
    CalendarAccountUiStateLogic by calendarAccountUiStateLogic {

    init {
        initCalendarAccountUiState()
    }
}
