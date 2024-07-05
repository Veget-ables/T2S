package com.tsuchinoko.t2s

import org.json.JSONObject
import java.time.LocalDateTime

fun JSONObject.toScheduleEvent(): ScheduleEvent {
    return ScheduleEvent(
        title = getString("title"),
        start = LocalDateTime.parse(getString("start")),
        end = LocalDateTime.parse(getString("end"))
    )
}

data class ScheduleEvent(
    val title: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
) {
    val isAllDay = start.hour == 0 && start.minute == 0 && end.hour == 23 && end.minute == 59
}
