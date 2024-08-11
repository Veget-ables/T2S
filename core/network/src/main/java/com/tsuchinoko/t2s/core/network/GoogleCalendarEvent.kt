package com.tsuchinoko.t2s.core.network

import android.annotation.SuppressLint
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

internal fun ScheduleEvent.convertToGoogleCalendarEvent(): Event {
    val (startDateTime, endDateTime) = if (isAllDay) {
        val startDateTime = EventDateTime()
            .setDate(
                DateTime(start.toLocalDate().toString())
            )
        startDateTime to startDateTime
    } else {
        val startDateTime = start.toEventDateTime()
        val endDateTime = end.toEventDateTime()
        startDateTime to endDateTime
    }

    return Event()
        .setStart(startDateTime)
        .setEnd(endDateTime)
        .setSummary(title)
}

@SuppressLint("SimpleDateFormat")
private fun LocalDateTime.toEventDateTime(): EventDateTime {
    val zonedDateTime = atZone(ZoneId.systemDefault())
    val date = Date.from(zonedDateTime.toInstant())
    return EventDateTime()
        .setDateTime(
            DateTime(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(date)
            )
        )
}
