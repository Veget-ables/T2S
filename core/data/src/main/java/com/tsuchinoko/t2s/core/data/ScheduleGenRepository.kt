package com.tsuchinoko.t2s.core.data

import com.tsuchinoko.t2s.core.model.ScheduleEvent

interface ScheduleGenRepository {
    suspend fun generate(prompt: String): List<ScheduleEvent>
}
