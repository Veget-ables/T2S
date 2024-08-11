package com.tsuchinoko.t2s.core.model

import java.time.LocalDateTime
import java.util.UUID

data class ScheduleEvent(
    val id: UUID,
    val title: String,
    val memo: String?,
    val start: LocalDateTime,
    val end: LocalDateTime,
) {
    val isAllDay =
        start.hour == 0 && start.minute == 0 && end.hour == 23 && end.minute == 59
}
