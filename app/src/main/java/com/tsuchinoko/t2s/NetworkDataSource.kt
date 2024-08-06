package com.tsuchinoko.t2s

interface NetworkDataSource {
    suspend fun getCalendars(): List<Calendar>
}
