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
「# 予定表のメモ書き」の内容を整理して、予定毎にカレンダーに登録したいので「# 予定表のメモ書き」の内容を「# フォーマット」の形に変換してください。
変換方法は「# 変換のルール」に従ってください。

# 予定表のメモ書き
$scheduleInput

### 予定表のメモ書きここまで ###

# フォーマット
- title: <予定のタイトル>
- memo: <予定のメモ>
- start: <予定の開始日時>
- end: <予定の終了日時>
- base:<title, memo, start, endを決定する証拠となったテキスト>

### フォーマットここまで ###

# 変換のルール
・1つの日付に対して複数の予定が含まれている場合がある
・予定の区切り位置は「予定のタイトルを表す絵文字 + 人物名やイベント名や場所名や固有名詞」
・時間帯に応じた行動リストが無い場合は終日予定と判断する
・フォーマットのルール
　・titleのルール
　　・人物名や場所名も含める
　・memoのルール
　　・時間帯に応じた行動リストも含める
　　・何もなければ省略する
　・startとendのルール
　　・必ずyyyy-MM-ddTHH:mmのフォーマットで入力する
　　・西暦が判断できない場合は、西暦（yyyy）に${year}を入力する
　　・月日が判断できない場合は、前後の予定から推測して入力する
　　・終日予定と判断した場合は、startの時刻(HH:mm)には00:00、endの時刻(HH:mm)には23:59を入力する
　　・終日予定以外で、時間帯に応じた行動リストがある場合は、行動リストの一番最初の時刻をstartの時刻(HH:mm)に入力して、最後の時刻をendの時刻(THH:mm)に入力する
　　　例えば、次の時間帯に応じた行動リストの場合、startの時刻(HH:mm)は10:30を入力し、endの時刻(HH:mm)は13:55を入力する
　　　　・10:30入り
　　　　・11:40 全体打ち合わせ
　　　　・11:55-13:55 生放送
　・baseのルール
　　・フォーマットのtitle, memo, start, endを決定する証拠となったテキストを入力する

### 変換のルールここまで ###
"""
}
