package com.tsuchinoko.t2s.feature.schedule.input

import androidx.lifecycle.ViewModel
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountUiStateLogic
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ScheduleInputViewModel @Inject constructor(
    calendarAccountUiStateLogic: CalendarAccountUiStateLogic,
) : ViewModel(),
    CalendarAccountUiStateLogic by calendarAccountUiStateLogic {

    init {
        initCalendarAccountUiState()
    }
}
