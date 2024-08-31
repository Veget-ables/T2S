package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tsuchinoko.t2s.core.designsystem.component.placeholder
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.core.model.ScheduleEvent

@Composable
internal fun CarouselScheduleGenContent(
    paddingValues: PaddingValues,
    prompt: String,
    generatedEventsUiState: GeneratedEventsUiState,
    onEventChange: (ScheduleEvent) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        OutlinedTextField(
            value = prompt,
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
    onEventChange: (ScheduleEvent) -> Unit = {},
) {
    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { events.count() },
        preferredItemWidth = 600.dp,
        modifier = modifier,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) { i ->
        GeneratedEvent(
            event = events[i],
            modifier = Modifier
                .fillMaxWidth()
                .maskClip(CardDefaults.shape)
                .padding(start = if (i == 0 || events.lastIndex == i) 0.dp else 16.dp)
                .placeholder(visible = placeholder),
            onEventChange = onEventChange,
        )
    }
}

@Preview
@Composable
fun CarouselScheduleGenContentPreview_Loading() {
    T2STheme {
        Surface {
            CarouselScheduleGenContent(
                paddingValues = PaddingValues(0.dp),
                prompt = fakePrompt,
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
                prompt = fakePrompt,
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
                prompt = fakePrompt,
                generatedEventsUiState = GeneratedEventsUiState.Error("予定の生成に失敗しました"),
            )
        }
    }
}
