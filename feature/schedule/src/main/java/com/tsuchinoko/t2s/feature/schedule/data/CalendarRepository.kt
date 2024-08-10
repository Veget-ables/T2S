package com.tsuchinoko.t2s.feature.schedule.data

import com.tsuchinoko.t2s.core.model.Calendar

interface CalendarRepository {
    suspend fun fetchCalendars(): List<Calendar>
}