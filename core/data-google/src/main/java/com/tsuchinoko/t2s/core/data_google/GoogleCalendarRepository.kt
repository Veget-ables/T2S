package com.tsuchinoko.t2s.core.data_google

import com.tsuchinoko.t2s.core.data.CalendarRepository
import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.CalendarId
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import com.tsuchinoko.t2s.core.network.GoogleCalendarDataSource
import javax.inject.Inject

class GoogleCalendarRepository @Inject constructor(
    private val networkSource: GoogleCalendarDataSource
) : CalendarRepository {
    override suspend fun fetchCalendars(): List<Calendar> {
        return networkSource.getCalendars()
    }

    override suspend fun registryEvents(calendarId: CalendarId, events: List<ScheduleEvent>) {
        networkSource.insertEvents(calendarId = calendarId, events = events)
    }
}
