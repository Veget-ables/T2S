package com.tsuchinoko.t2s.core.network

import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.CalendarId
import com.tsuchinoko.t2s.core.model.ScheduleEvent

interface CalendarNetworkDataSource {
    suspend fun getCalendars(): List<Calendar>
    suspend fun insertEvents(calendarId: CalendarId, events: List<ScheduleEvent>)
}
