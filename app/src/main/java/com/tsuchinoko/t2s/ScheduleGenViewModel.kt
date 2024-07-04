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

                _uiState.value = UiState.Success(scheduleEvents)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    companion object {
        const val SCHEDULE_GENERATIVE_PROMPT = """
あなたはGoogle Calendarに予定を登録したい人です。
以下の予定のリストを### フォーマット ###に変換してください。
変換方法は### 例 ### のように行ってください。

### フォーマット ###
- title: <予定のタイトル>
- start(yyyy-mm-dd hh:mm): <予定の開始日時>
- end(yyyy-mm-dd hh:mm): <予定の終了日時>
※ start、endが不明な場合は省略

#######

### 変換例 ###

<例1>
- 変換前: 
　●6/25(火)
　OFF①

- 変換後: 
　タイトル: OFF①
　開始日時: 2024-06-25 00:00
　終了日時: 2024-06-25 23:59

<例2>
- 変換前: 
　●6/26(水)
　▼【武田】TKM「ラジオ」＠TKM
　11:20 TOKYO FM入り
　14:20 生放送出演
　14:45 終了
　▼【武田】TKM「Youtube」撮影

- 変換後:
　タイトル:【武田】TKM「ラジオ」＠TKM
　開始日時: 2024-06-26 11:20
　終了日時: 2024-06-26 14:45

　タイトル:【武田】TKM「Youtube」撮影
　開始日時: 2024-06-26 00:00
　終了日時: 2024-06-26 23:59

<例3>
- 変換前: 
　●6/30(日)
　▼【武田】HBC「サタブラ」＠HBC
　10:30入り
　11:55-13:30生放送

- 変換後:
　タイトル:【武田】HBC「サタブラ」＠HBC
　開始日時: 2024-06-30 10:30
　終了日時: 2024-06-30 13:30

<例4>
- 変換前: 
　●7/4(木)
　▼【武田】TJK「BreakingDown」＠荻窪
　08:00 入り
　10:15-18:55 生放送

- 変換後:
　タイトル: 【武田】TJK「BreakingDown」＠荻窪
　開始日時: 2024-07-04 08:00
　終了日時: 2024-07-04 18:55

#######

----- 以下、予定のリスト -----
"""
    }
}

