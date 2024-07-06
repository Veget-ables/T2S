package com.tsuchinoko.t2s

import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

fun JSONObject.toScheduleEvent(): ScheduleEvent {
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

data class ScheduleEvent(
    val id: UUID,
    val title: String,
    val memo: String?,
    val start: LocalDateTime,
    val end: LocalDateTime,
) {
    private val isAllDay =
        start.hour == 0 && start.minute == 0 && end.hour == 23 && end.minute == 59

    val displayDateTime: DisplayDateTime = if (isAllDay) {
        DisplayDateTime.AllDay(localDate = start.toLocalDate())
    } else {
        DisplayDateTime.Regular(start = start, end = end)
    }
}

sealed interface DisplayDateTime {
    val value: String

    data class AllDay(val localDate: LocalDate) : DisplayDateTime {
        override val value: String = localDate.format(DateTimeFormatter.ofPattern("MM/dd(E)"))
    }

    data class Regular(
        val start: LocalDateTime,
        val end: LocalDateTime,
    ) : DisplayDateTime {
        private val displayStart = start.format(DateTimeFormatter.ofPattern("MM/dd(E) HH:mm"))

        private val displayEnd = if (start.toLocalDate() == end.toLocalDate()) {
            end.format(DateTimeFormatter.ofPattern("HH:mm"))
        } else {
            end.format(DateTimeFormatter.ofPattern("MM/dd(E) HH:mm"))
        }

        override val value = "$displayStart 〜 $displayEnd"
    }
}
