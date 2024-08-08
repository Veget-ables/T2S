package com.tsuchinoko.t2s.data

import com.tsuchinoko.t2s.model.Calendar
import com.tsuchinoko.t2s.network.GoogleServiceDataSource
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(
    private val serviceDataSource: GoogleServiceDataSource
) : CalendarRepository {
    override suspend fun fetchCalendars(): List<Calendar> {
        return serviceDataSource.getCalendars()
    }
}
