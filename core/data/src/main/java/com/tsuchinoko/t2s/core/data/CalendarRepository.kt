package com.tsuchinoko.t2s.core.data

import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.CalendarId
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    fun getAccountCalendars(): Flow<List<Calendar>>
    suspend fun fetchCalendars()
    suspend fun setTargetCalendar(calendarId: CalendarId)
    suspend fun registryEvents(calendarId: CalendarId, events: List<ScheduleEvent>)
}
