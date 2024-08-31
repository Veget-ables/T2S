package com.tsuchinoko.t2s.core.network

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.InvalidStateException
import com.google.ai.client.generativeai.type.content
import com.tsuchinok.t2s.core.common.Dispatcher
import com.tsuchinok.t2s.core.common.T2SDispatchers
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GeminiScheduleGenDataSource @Inject constructor(
    @Dispatcher(T2SDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val generativeModel: GenerativeModel,
) : ScheduleGenNetworkDataSource {
    override suspend fun generate(prompt: String): List<ScheduleEvent> {
        return withContext(ioDispatcher) {
            val response = generativeModel.generateContent(
                content {
                    text(prompt)
                },
            )
            val scheduleEvents = response.functionCalls.map { functionCall ->
                val matchedFunction = generativeModel.tools?.flatMap { it.functionDeclarations }
                    ?.first { it.name == functionCall.name }
                    ?: throw InvalidStateException("Function not found: ${functionCall.name}")

                val scheduleJson = matchedFunction.execute(functionCall)
                scheduleJson.toScheduleEvent()
            }
            return@withContext scheduleEvents
        }
    }
}
