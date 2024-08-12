package com.tsuchinoko.t2s.core.network

import com.tsuchinoko.t2s.core.model.ScheduleEvent

interface ScheduleGenNetworkDataSource {
    suspend fun generate(prompt: String): List<ScheduleEvent>
}
