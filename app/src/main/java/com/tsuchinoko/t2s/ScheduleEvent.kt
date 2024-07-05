package com.tsuchinoko.t2s

import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun JSONObject.toScheduleEvent(): ScheduleEvent {
    val title = getString("title")
    val start = LocalDateTime.parse(getString("start"))
    val end = LocalDateTime.parse(getString("end"))
    val isAllDay = start.hour == 0 && start.minute == 0 && end.hour == 23 && end.minute == 59
    return if (isAllDay) {
        AllDayEvent(title = title, date = start.toLocalDate())
    } else {
        RegularEvent(title = title, start = start, end = end)
    }
}

sealed interface ScheduleEvent {
    val title: String
}

data class AllDayEvent(
    override val title: String,
    val date: LocalDate,
) : ScheduleEvent {
    val displayDate = date.format(DateTimeFormatter.ofPattern("MM/dd(E)"))
}

data class RegularEvent(
    override val title: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
) : ScheduleEvent {
    val displayStart = start.format(DateTimeFormatter.ofPattern("MM/dd(E) HH:mm"))

    val displayEnd = if (start.toLocalDate() == end.toLocalDate()) {
        end.format(DateTimeFormatter.ofPattern("HH:mm"))
    } else {
        end.format(DateTimeFormatter.ofPattern("MM/dd/(E) HH:mm"))
    }
}
