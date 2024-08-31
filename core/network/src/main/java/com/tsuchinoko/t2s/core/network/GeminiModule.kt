package com.tsuchinoko.t2s.core.network

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.FunctionCallPart
import com.google.ai.client.generativeai.type.FunctionDeclaration
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
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
        Schema.str(name = "start", description = "予定の開始日時"),
        Schema.str(name = "end", description = "予定の終了日時"),
        Schema.str(name = "base", description = "title, memo, start, endを決定する証拠となったテキスト"),
    ) { title, memo, start, end, base ->
        JSONObject().apply {
            put("title", title)
            if (memo == EMPTY_SYMBOL) put("memo", "") else put("memo", memo)
            put("start", start)
            put("end", end)
            put("base", base)
        }
    }
}

private fun <T, U, V, W, X> defineFunction(
    name: String,
    description: String,
    arg1: Schema<T>,
    arg2: Schema<U>,
    arg3: Schema<V>,
    arg4: Schema<W>,
    arg5: Schema<X>,
    function: suspend (T, U, V, W, X) -> JSONObject,
) = FiveParameterFunction(name, description, arg1, arg2, arg3, arg4, arg5, function)

private class FiveParameterFunction<T, U, V, W, X>(
    name: String,
    description: String,
    val param1: Schema<T>,
    val param2: Schema<U>,
    val param3: Schema<V>,
    val param4: Schema<W>,
    val param5: Schema<X>,
    val function: suspend (T, U, V, W, X) -> JSONObject,
) : FunctionDeclaration(name, description) {
    override fun getParameters() = listOf(param1, param2, param3, param4, param5)

    override suspend fun execute(part: FunctionCallPart): JSONObject {
        val arg1 = part.getArgOrThrow(param1)
        val arg2 = part.getArgOrThrow(param2)
        val arg3 = part.getArgOrThrow(param3)
        val arg4 = part.getArgOrThrow(param4)
        val arg5 = part.getArgOrThrow(param5)
        return function(arg1, arg2, arg3, arg4, arg5)
    }
}

private fun <T> FunctionCallPart.getArgOrThrow(param: Schema<T>): T = param.fromString(args[param.name])
    ?: throw RuntimeException(
        "Missing argument for parameter \"${param.name}\" for function \"$name\"",
    )
