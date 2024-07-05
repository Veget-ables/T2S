package com.tsuchinoko.t2s

import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun JSONObject.toScheduleEvent(): ScheduleEvent {
    val title = getString("title").ifBlank { "タイトルなし" }
    val memo = getString("memo").ifBlank { null }
    val start = LocalDateTime.parse(getString("start"))
    val end = LocalDateTime.parse(getString("end"))
    return ScheduleEvent(
        title = title,
        memo = memo,
        start = start,
        end = end
    )
}

data class ScheduleEvent(
    val title: String,
    val memo: String?,
    val start: LocalDateTime,
    val end: LocalDateTime,
) {
    private val isAllDay =
        start.hour == 0 && start.minute == 0 && end.hour == 23 && end.minute == 59

    val simpleStartDate = start.format(DateTimeFormatter.ofPattern("yyyy/MM/dd(E)"))
    val simpleStartTime = start.format(DateTimeFormatter.ofPattern("HH:mm"))

    val simpleEndDate = end.format(DateTimeFormatter.ofPattern("yyyy/MM/dd(E)"))
    val simpleEndTime = end.format(DateTimeFormatter.ofPattern("HH:mm"))

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
