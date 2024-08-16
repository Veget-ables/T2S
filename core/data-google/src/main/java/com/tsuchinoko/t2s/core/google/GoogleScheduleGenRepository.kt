package com.tsuchinoko.t2s.core.google

import com.tsuchinoko.t2s.core.data.ScheduleGenRepository
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import com.tsuchinoko.t2s.core.network.GeminiScheduleGenDataSource
import javax.inject.Inject

class GoogleScheduleGenRepository @Inject constructor(
    private val networkSource: GeminiScheduleGenDataSource,
) : ScheduleGenRepository {
    override suspend fun generate(prompt: String): List<ScheduleEvent> = networkSource.generate(prompt)
}
