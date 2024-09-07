package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import com.tsuchinoko.t2s.core.model.BaseInput

@Composable
internal fun rememberHighlightTextValue(
    scheduleInput: String,
    baseInput: BaseInput?,
): MutableState<TextFieldValue> {
    if (baseInput == null) return remember { mutableStateOf(TextFieldValue(scheduleInput)) }

    val splitTextByDate = scheduleInput.split(baseInput.date)
    val splitTextByTitle = splitTextByDate[1].split(baseInput.title)

    val highlightTextFieldValue = TextFieldValue(
        annotatedString = buildAnnotatedString {
            append(splitTextByDate[0])
            withStyle(
                style = SpanStyle(
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                ),
            ) {
                append(baseInput.date)
            }

            append(splitTextByTitle[0])

            withStyle(
                style = SpanStyle(
                    color = Color.Blue,
                    fontWeight = FontWeight.Bold,
                ),
            ) {
                append(baseInput.title)
            }

            append(splitTextByTitle[1])
        },
        selection = TextRange(splitTextByDate[0].length + baseInput.date.length + splitTextByTitle[0].length + baseInput.title.length),
    )
    return remember(baseInput) { mutableStateOf(highlightTextFieldValue) }
}
