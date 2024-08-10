package com.tsuchinoko.t2s.feature.schedule.network

import android.content.Intent
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.calendar.model.CalendarList
import com.tsuchinoko.t2s.feature.schedule.model.Calendar
import com.tsuchinoko.t2s.feature.schedule.common.Dispatcher
import com.tsuchinoko.t2s.feature.schedule.common.T2SDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GoogleServiceDataSource @Inject constructor(
    @Dispatcher(T2SDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val service: com.google.api.services.calendar.Calendar
) : NetworkDataSource {
    override suspend fun getCalendars(): List<Calendar> {
        return withContext(ioDispatcher) {
            try {
                val calendars = service.calendarList().list().execute()
                return@withContext calendars.convert()
            } catch (e: UserRecoverableAuthIOException) {
                throw GoogleServiceRecoverableError(
                    message = e.message,
                    intent = e.intent
                )
            }
        }
    }
}

data class GoogleServiceRecoverableError(
    override val message: String?,
    val intent: Intent
): Exception()

private fun CalendarList.convert(): List<Calendar> {
    return this.items.map { item ->
        Calendar(title = item.summary)
    }
}
