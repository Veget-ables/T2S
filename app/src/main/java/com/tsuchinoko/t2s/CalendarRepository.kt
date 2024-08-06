package com.tsuchinoko.t2s

import com.google.api.services.calendar.model.Calendar

interface CalendarRepository {
    suspend fun fetchCalendars(): List<Calendar>
}