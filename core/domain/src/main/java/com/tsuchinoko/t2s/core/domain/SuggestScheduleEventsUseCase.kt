package com.tsuchinoko.t2s.core.domain

import android.icu.util.Calendar
import com.tsuchinoko.t2s.core.data.ScheduleGenRepository
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import javax.inject.Inject

class SuggestScheduleEventsUseCase @Inject constructor(
    private val scheduleGenRepository: ScheduleGenRepository,
) {
    private val year = Calendar.getInstance().get(Calendar.YEAR)

    suspend operator fun invoke(scheduleInput: String): List<ScheduleEvent> {
        val prompt = createPrompt(scheduleInput)
        return scheduleGenRepository.generate(prompt)
    }

    private fun createPrompt(scheduleInput: String) = """
「# 予定表のメモ書き」の内容を「# 予定フォーマット」の形に変換してください。
変換方法は「# 予定フォーマットへの変換の観点」に従ってください。

# 予定表のメモ書き
$scheduleInput

### 予定表のメモ書きここまで ###

# 予定フォーマット
- title: <予定のタイトル>
- memo: <予定のメモ>
- start: <予定の開始日時>
- end: <予定の終了日時>
- base:<予定フォーマットの「title」を決定する証拠となった元のテキスト>

### 予定フォーマットここまで ###

# 予定フォーマットへの変換の観点
・1つの日付に対して複数の予定が含まれている場合がある
・予定の区切り位置は「タイトルを象徴する絵文字 + 人物名やイベント名や場所名や固有名詞を組みあせた文字列」
・予定毎に「# 予定フォーマットのルール」を適用する

### 予定フォーマットへの変換の観点ここまで ###

## 予定フォーマットのルール（優先度順）
・「title」のルール
　・「タイトルを象徴する絵文字 + 人物名やイベント名や場所名や固有名詞を組みあせた文字列」が当てはまる可能性が高い 
　・元のテキストの情報は「タイトルを象徴する絵文字」のみを省略してそれ以外は編集せずにそのまま入力する
・「memo」のルール
　・時間帯に応じた行動リストを入力する
　・何もなければ、"メモはなし"を入力する
・「start」と「end」のルール
　・必ずyyyy-MM-ddTHH:mmのフォーマットで入力する
　・西暦が判断できない場合は、西暦（yyyy）に${year}を入力する
　・月日が判断できない場合は、前後の予定から推測して入力する
　・時間帯に応じた行動リストが無い場合は終日予定と判断する
　・終日予定と判断した場合は「start」の時刻(HH:mm)は00:00を入力し、「end」の時刻(HH:mm)は23:59を入力する
　・終日予定以外で、時間帯に応じた行動リストがある場合は「start」の時刻(HH:mm)は行動リストの一番最初の時刻を入力し、「end」の時刻(THH:mm)は行動リストの最後の時刻を入力する
　　・例えば、次の時間帯に応じた行動リストの場合、「start」の時刻(HH:mm)は10:30を入力し、「end」の時刻(HH:mm)は13:55を入力する
　　　・10:30入り
　　　・11:55-13:55 生放送
・「base」のルール
　・「start」の日付（yyyy-MM-dd）と「end」の日付（yyyy-MM-dd）の証拠となった元のテキストは省略する
　・予定フォーマットの「title」を決定する証拠となった元のテキストを入力する

### 予定フォーマットのルールここまで ###
"""
}
