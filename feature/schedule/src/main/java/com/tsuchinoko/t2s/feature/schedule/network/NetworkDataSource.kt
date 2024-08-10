package com.tsuchinoko.t2s.feature.schedule.network

import com.tsuchinoko.t2s.core.model.Calendar

interface NetworkDataSource {
    suspend fun getCalendars(): List<Calendar>
}
