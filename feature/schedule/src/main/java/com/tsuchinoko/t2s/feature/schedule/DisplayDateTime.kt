package com.tsuchinoko.t2s.feature.schedule

import com.tsuchinoko.t2s.core.model.ScheduleEvent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal val ScheduleEvent.displayDateTime: DisplayDateTime
    get() = if (isAllDay) {
        DisplayDateTime.AllDay(localDate = start.toLocalDate())
    } else {
        DisplayDateTime.Regular(start = start, end = end)
    }

internal sealed interface DisplayDateTime {
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
