package com.tsuchinoko.t2s.feature.schedule.data

import com.tsuchinoko.t2s.feature.schedule.model.Calendar
import com.tsuchinoko.t2s.feature.schedule.network.GoogleServiceDataSource
import javax.inject.Inject

class CalendarRepositoryImpl @Inject constructor(
    private val serviceDataSource: GoogleServiceDataSource
) : CalendarRepository {
    override suspend fun fetchCalendars(): List<Calendar> {
        return serviceDataSource.getCalendars()
    }
}
