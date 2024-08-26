package com.tsuchinoko.t2s.core.google

import com.tsuchinoko.core.database.dao.CalendarDao
import com.tsuchinoko.core.database.entity.convertToEntity
import com.tsuchinoko.core.database.entity.convertToModel
import com.tsuchinoko.t2s.core.data.CalendarRepository
import com.tsuchinoko.t2s.core.datastore.T2SPreferencesDataStore
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.CalendarId
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import com.tsuchinoko.t2s.core.network.GoogleCalendarDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoogleCalendarRepository @Inject constructor(
    private val calendarDao: CalendarDao,
    private val dataStore: T2SPreferencesDataStore,
    private val networkSource: GoogleCalendarDataSource,
) : CalendarRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAccountCalendars(): Flow<List<Calendar>> = dataStore.getTargetAccountId()
        .flatMapLatest { accountId ->
            if (accountId == null) {
                flow { emit(emptyList()) }
            } else {
                calendarDao.get(accountId.value).map {
                    it.map { it.convertToModel() }
                }
            }
        }

    override fun getTargetCalendarId(): Flow<CalendarId?> = dataStore.getTargetCalendarId()

    override suspend fun fetchCalendars(account: Account) {
        val calendars = networkSource.getCalendars()
        val entities = calendars.map { it.convertToEntity(account) }
        calendarDao.insertAll(entities)
    }

    override suspend fun setTargetCalendar(calendar: Calendar) {
        dataStore.setTargetCalendar(calendar)
    }

    override suspend fun registryEvents(calendarId: CalendarId, events: List<ScheduleEvent>) {
        networkSource.insertEvents(calendarId = calendarId, events = events)
    }
}
