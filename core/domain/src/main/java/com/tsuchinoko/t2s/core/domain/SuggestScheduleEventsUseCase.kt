package com.tsuchinoko.t2s.core.domain

import com.tsuchinok.t2s.core.common.EMPTY_SYMBOL
import com.tsuchinoko.t2s.core.data.ScheduleGenRepository
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import javax.inject.Inject

class SuggestScheduleEventsUseCase @Inject constructor(
    private val scheduleGenRepository: ScheduleGenRepository,
) {
    suspend operator fun invoke(scheduleInput: String): List<ScheduleEvent> {
        val prompt = createPrompt(scheduleInput)
        return scheduleGenRepository.generate(prompt)
    }

    private fun createPrompt(scheduleInput: String) = """
「# 予定表のメモ書き」の内容を整理して、予定毎にカレンダーに登録したいので「# 予定表のメモ書き」の内容を「# フォーマット」の形に変換してください。
変換方法は「# 変換例」のように行ってください。

# メモ書き
$scheduleInput

### 予定表のメモ書きここまで ###

# フォーマット
- title: <予定のタイトル>
- memo: <予定のメモ>
- start(yyyy-MM-ddTHH:mm:ss): <予定の開始日時>
- end(yyyy-MM-ddTHH:mm:ss): <予定の終了日時>
※ memoは情報が無ければ省略
※ start、endが不明な場合は省略

### フォーマットここまで ###

# 変換例

<例1>
- 変換前: 
　●6/25(火)
　OFF①

- 変換後: 
　title: OFF①
  memo: $EMPTY_SYMBOL
　start: 2024-06-25 00:00
　end: 2024-06-25 23:59

<例2>
- 変換前: 
　●6/26(水)
　▼【武田】TKM「ラジオ」＠TKM
　11:20 TOKYO FM入り
　14:20 生放送出演
　14:45 終了
　▼【武田】TKM「Youtube」撮影

- 変換後:
　title:【武田】TKM「ラジオ」＠TKM
　memo:　11:20 TOKYO FM入り
　　　　　14:20 生放送出演
　　　　　14:45 終了
　start: 2024-06-26 11:20
　end: 2024-06-26 14:45

　title:【武田】TKM「Youtube」撮影
　start: 2024-06-26 00:00
　end: 2024-06-26 23:59

<例3>
- 変換前: 
　●6/30(日)
　▼【武田】HBC「サタブラ」＠HBC
　10:30入り
　11:55-13:30生放送

- 変換後:
　title:【武田】HBC「サタブラ」＠HBC
  memo: 10:30入り
        11:55-13:30生放送
　start: 2024-06-30 10:30
　end: 2024-06-30 13:30

<例4>
- 変換前: 
　●7/4(木)
　▼【武田】TJK「BreakingDown」＠荻窪
　08:00 入り
　10:15-18:55 生放送

- 変換後:
　title: 【武田】TJK「BreakingDown」＠荻窪
  memo: 08:00 入り
　      10:15-18:55 生放送
　start: 2024-07-04 08:00
　end: 2024-07-04 18:55

### 変換例ここまで ###

"""
}
