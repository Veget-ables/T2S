package com.tsuchinoko.t2s.core.network

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.calendar.model.CalendarList
import com.tsuchinok.t2s.core.common.Dispatcher
import com.tsuchinok.t2s.core.common.T2SDispatchers
import com.tsuchinok.t2s.core.common.error.RecoverableIntentError
import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.CalendarId
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GoogleCalendarDataSource @Inject constructor(
    @Dispatcher(T2SDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val service: com.google.api.services.calendar.Calendar,
) : CalendarNetworkDataSource {
    override suspend fun getCalendars(): List<Calendar> {
        return withContext(ioDispatcher) {
            try {
                val calendars = service.calendarList().list().execute()
                return@withContext calendars.convert()
            } catch (e: UserRecoverableAuthIOException) {
                throw RecoverableIntentError(
                    message = e.message,
                    intent = e.intent,
                )
            }
        }
    }

    override suspend fun insertEvents(calendarId: CalendarId, events: List<ScheduleEvent>) =
        withContext(ioDispatcher) {
            events.forEach { event ->
                val googleCalendarEvent = event.convertToGoogleCalendarEvent()
                service
                    .events()
                    .insert(calendarId.value, googleCalendarEvent)
                    .execute()
            }
        }
}

private fun CalendarList.convert(): List<Calendar> = this.items.map { item ->
    Calendar(
        id = CalendarId(item.id),
        title = item.summary,
    )
}
