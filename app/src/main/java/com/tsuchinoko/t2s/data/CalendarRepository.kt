package com.tsuchinoko.t2s.data

import com.tsuchinoko.t2s.model.Calendar

interface CalendarRepository {
    suspend fun fetchCalendars(): List<Calendar>
}