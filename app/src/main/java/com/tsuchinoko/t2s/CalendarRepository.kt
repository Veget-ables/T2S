package com.tsuchinoko.t2s

interface CalendarRepository {
    suspend fun fetchCalendars(): List<Calendar>
}