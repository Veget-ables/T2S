package com.tsuchinoko.t2s.core.data

import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.ScheduleEvent

interface CalendarRepository {
    suspend fun fetchCalendars(): List<Calendar>
    suspend fun registryEvents(calendarId: String, events: List<ScheduleEvent>)
}