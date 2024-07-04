package com.tsuchinoko.t2s

import org.json.JSONObject

fun JSONObject.toScheduleEvent(): ScheduleEvent {
    return ScheduleEvent(
        title = getString("title"),
        start = getString("start"),
        end = getString("end")
    )
}

data class ScheduleEvent(
    val title: String,
    val start: String,
    val end: String,
)
