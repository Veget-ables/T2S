package com.tsuchinoko.t2s.core.network

import com.tsuchinoko.t2s.core.model.BaseInput
import com.tsuchinoko.t2s.core.model.EventId
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.UUID

internal fun JSONObject.toScheduleEvent(): ScheduleEvent {
    val title = getString("title").ifBlank { "タイトルなし" }
    val memo = getString("memo").ifBlank { "メモなし" }
    val baseTitle = getString("baseTitle").ifBlank { "" }
    val baseDate = getString("baseDate").ifBlank { "" }
    val start = LocalDateTime.parse(getString("start"))
    val end = LocalDateTime.parse(getString("end"))
    return ScheduleEvent(
        id = EventId(UUID.randomUUID()),
        title = title,
        memo = memo,
        start = start,
        end = end,
        base = BaseInput(title = baseTitle, date = baseDate),
    )
}
