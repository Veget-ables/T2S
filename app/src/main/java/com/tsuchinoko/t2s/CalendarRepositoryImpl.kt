package com.tsuchinoko.t2s

import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(
    private val serviceDataSource: GoogleServiceDataSource
) : CalendarRepository {
    override suspend fun fetchCalendars(): List<Calendar> {
        return serviceDataSource.getCalendars()
    }
}
