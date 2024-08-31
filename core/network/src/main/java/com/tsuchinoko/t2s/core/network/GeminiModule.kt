package com.tsuchinoko.t2s.core.network

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.defineFunction
import com.tsuchinok.t2s.core.common.EMPTY_SYMBOL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.json.JSONObject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GeminiModule {
    @Singleton
    @Provides
    fun provideGenerativeModel(): GenerativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey,
        tools = listOf(Tool(listOf(getScheduleStructure))),
        systemInstruction = content { text("あなたはカレンダーに予定を登録しようとしている人です。") },
    )

    private val getScheduleStructure = defineFunction(
        name = "getScheduleStructure",
        description = "予定を表す文字列から整理された予定のJson構造を取得する",
        Schema.str(name = "title", description = "予定のタイトル"),
        Schema.str(name = "memo", description = "予定のメモ"),
        Schema.str(name = "start", description = "開始時間"),
        Schema.str(name = "end", description = "終了時間"),
    ) { title, memo, start, end ->
        JSONObject().apply {
            put("title", title)
            if (memo == EMPTY_SYMBOL) put("memo", "") else put("memo", memo)
            put("start", start)
            put("end", end)
        }
    }
}
