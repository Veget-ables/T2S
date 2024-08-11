package com.tsuchinoko.t2s.feature.schedule

import com.tsuchinoko.t2s.core.model.ScheduleEvent
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.UUID

internal fun JSONObject.toScheduleEvent(): ScheduleEvent {
    val title = getString("title").ifBlank { "タイトルなし" }
    val memo = getString("memo").ifBlank { null }
    val start = LocalDateTime.parse(getString("start"))
    val end = LocalDateTime.parse(getString("end"))
    return ScheduleEvent(
        id = UUID.randomUUID(),
        title = title,
        memo = memo,
        start = start,
        end = end
    )
}
