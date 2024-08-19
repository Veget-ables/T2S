package com.tsuchinoko.t2s.core.google

import com.tsuchinoko.core.database.dao.CalendarDao
import com.tsuchinoko.core.database.entity.convertToModel
import com.tsuchinoko.t2s.core.data.CalendarRepository
import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.CalendarId
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import com.tsuchinoko.t2s.core.network.GoogleCalendarDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoogleCalendarRepository @Inject constructor(
    private val calendarDao: CalendarDao,
    private val networkSource: GoogleCalendarDataSource,
) : CalendarRepository {
    override fun getAccountCalendars(): Flow<List<Calendar>> = calendarDao.getAll()
        .map {
            it.map { it.convertToModel() }
        }

    override suspend fun fetchCalendars(): List<Calendar> = networkSource.getCalendars()

    override suspend fun setTargetCalendar(calendarId: CalendarId) {
        // TODO
    }

    override suspend fun registryEvents(calendarId: CalendarId, events: List<ScheduleEvent>) {
        networkSource.insertEvents(calendarId = calendarId, events = events)
    }
}
