package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tsuchinoko.t2s.core.designsystem.component.placeholder
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.core.model.ScheduleEvent

@Composable
internal fun CarouselScheduleGenContent(
    paddingValues: PaddingValues,
    scheduleInput: String,
    generatedEventsUiState: GeneratedEventsUiState,
    onEventChange: (ScheduleEvent) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        var baseText: String? by remember { mutableStateOf(null) }

        OutlinedTextField(
            value = TextFieldValue(
                buildAnnotatedString {
                    val base = baseText
                    if (base == null) {
                        append(scheduleInput)
                        return@buildAnnotatedString
                    } else {
                        val sentence = scheduleInput.split(base)
                        append(sentence[0])
                        withStyle(
                            style = SpanStyle(
                                color = Color.Blue,
                                fontWeight = FontWeight.Bold,
                            ),
                        ) {
                            append(base)
                        }
                        if (sentence.size > 1) {
                            append(sentence[1])
                        }
                    }
                },
            ),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(1f),
        )

        when (generatedEventsUiState) {
            GeneratedEventsUiState.Loading -> {
                CarouselScheduleEvents(
                    events = skeletonEvents,
                    placeholder = true,
                )
            }

            is GeneratedEventsUiState.Generated -> {
                CarouselScheduleEvents(
                    events = generatedEventsUiState.events,
                    placeholder = false,
                    onEventClick = { baseText = it },
                    onEventChange = onEventChange,
                )
            }

            is GeneratedEventsUiState.Error -> {
                Text(
                    text = generatedEventsUiState.message,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CarouselScheduleEvents(
    events: List<ScheduleEvent>,
    placeholder: Boolean,
    modifier: Modifier = Modifier,
    onEventClick: (baseText: String) -> Unit = {},
    onEventChange: (ScheduleEvent) -> Unit = {},
) {
    val carouselState = rememberCarouselState { events.count() }
    HorizontalMultiBrowseCarousel(
        state = carouselState,
        preferredItemWidth = 600.dp,
        modifier = modifier,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) { i ->
        val event = events[i]
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .maskClip(CardDefaults.shape)
                .padding(start = if (i == 0 || events.lastIndex == i) 0.dp else 16.dp)
                .placeholder(visible = placeholder)
                .animateContentSize(),
            onClick = {
                onEventClick(event.base)
            },
        ) {
            EditableEventContent(
                event = event,
                modifier = Modifier.fillMaxWidth(),
                onEventChange = onEventChange,
            )
        }
    }
}

@Preview
@Composable
fun CarouselScheduleGenContentPreview_Loading() {
    T2STheme {
        Surface {
            CarouselScheduleGenContent(
                paddingValues = PaddingValues(0.dp),
                scheduleInput = fakeScheduleInput,
                generatedEventsUiState = GeneratedEventsUiState.Loading,
            )
        }
    }
}

@Preview
@Composable
fun CarouselScheduleGenContentPreview_Generated_List() {
    T2STheme {
        Surface {
            CarouselScheduleGenContent(
                paddingValues = PaddingValues(0.dp),
                scheduleInput = fakeScheduleInput,
                generatedEventsUiState = GeneratedEventsUiState.Generated(fakeEvents),
            )
        }
    }
}

@Preview
@Composable
fun CarouselScheduleGenContentPreview_Error() {
    T2STheme {
        Surface {
            CarouselScheduleGenContent(
                paddingValues = PaddingValues(0.dp),
                scheduleInput = fakeScheduleInput,
                generatedEventsUiState = GeneratedEventsUiState.Error("予定の生成に失敗しました"),
            )
        }
    }
}
