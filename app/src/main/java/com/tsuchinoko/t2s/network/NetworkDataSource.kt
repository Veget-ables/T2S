package com.tsuchinoko.t2s.network

import com.tsuchinoko.t2s.model.Calendar

interface NetworkDataSource {
    suspend fun getCalendars(): List<Calendar>
}
