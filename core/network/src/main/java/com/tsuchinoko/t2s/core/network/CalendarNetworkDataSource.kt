package com.tsuchinoko.t2s.core.network

import com.tsuchinoko.t2s.core.model.Calendar

interface CalendarNetworkDataSource {
    suspend fun getCalendars(): List<Calendar>
}
