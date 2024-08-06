package com.tsuchinoko.t2s

import com.google.api.services.calendar.Calendar
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(private val calendarService: Calendar): CalendarRepository {
    override suspend fun fetchCalendars(): List<com.google.api.services.calendar.model.Calendar> {
        calendarService
        return emptyList()
    }
}
