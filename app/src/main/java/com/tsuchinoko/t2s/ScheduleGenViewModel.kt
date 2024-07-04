package com.tsuchinoko.t2s

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.InvalidStateException
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.defineFunction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class ScheduleGenViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val getScheduleStructure = defineFunction(
        name = "getScheduleStructure",
        description = "予定を表す文字列から整理された予定のJson構造を取得する",
        Schema.str(name = "title", description = "予定のタイトル"),
        Schema.str(name = "start", description = "開始時間"),
        Schema.str(name = "end", description = "終了時間"),
    ) { title, start, end ->
        JSONObject().apply {
            put("title", title)
            put("start", start)
            put("end", end)
        }.also {
            Log.d("JSON", it.toString())
        }
    }

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey,
        tools = listOf(Tool(listOf(getScheduleStructure)))
    )

    fun sendPrompt(prompt: String) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(
                    content {
                        text(SCHEDULE_GENERATIVE_PROMPT + prompt)
                    }
                )
                val scheduleEvents = response.functionCalls.map { functionCall ->
                    val matchedFunction = generativeModel.tools?.flatMap { it.functionDeclarations }
                        ?.first { it.name == functionCall.name }
                        ?: throw InvalidStateException("Function not found: ${functionCall.name}")

                    val scheduleJson = matchedFunction.execute(functionCall)
                    scheduleJson.toScheduleEvent()
                }

                response.text

                _uiState.value = UiState.Success(scheduleEvents)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    companion object {
        const val EXAMPLES =
                "input1: " +
                "●6/25(火)" +
                "OFF①" +
                "\n" +
                "output1: " +
                "タイトル: OFF①" +
                "開始日時: 2024-06-25 08:00" +
                "終了日時: 2024-06-25 23:59" +
                "\n\n" +
                "input2: " +
                "●6/26(水)" +
                "▼【武田】TKM「ラジオ」＠TKM" +
                "11:20 TOKYO FM入り" +
                "14:20 生放送出演" +
                "14:45 終了" +
                "▼【武田】TKM「Youtube」撮影" +
                "\n" +
                "output2: " +
                "タイトル:【武田】TKM「ラジオ」＠TKM" +
                "開始日時: 2024-06-26 11:20" +
                "終了日時: 2024-06-26 14:45" +
                "\n\n" +
                "タイトル:【武田】TKM「Youtube」撮影" +
                "開始日時: 2024-06-26 00:00" +
                "終了日時: 2024-06-26 23:59" +
                "\n" +
                "input3: " +
                "●7/4(木)" +
                "▼【武田】TJK「BreakingDown」＠荻窪" +
                "8:00 入り" +
                "10:15-18:55 生放送" +
                "\n" +
                "output3: " +
                "タイトル: 【武田】TJK「BreakingDown」＠荻窪" +
                "開始日時: 2024-07-04 08:00" +
                "終了日時: 2024-07-04 18:55"

        const val SCHEDULE_GENERATIVE_PROMPT = "" +
                "以下の文字列から、### 例 ###のようなルールで###制約###に従って予定のリストを作って" +
                "\n\n" +
                "### 例 ###" +
                "\n\n" +
                EXAMPLES +
                "\n\n" +
                "### 制約 ###" +
                "\n" +
                "- 予定ごとにタイトル, 開始日時(yyyy-mm-dd hh:mm), 終了日時(yyyy-mm-dd hh:mm)で表現する" +
                "\n" +
                "- 時間が不明な予定は終日として扱う" +
                "\n\n" +
                "-----"
    }
}

