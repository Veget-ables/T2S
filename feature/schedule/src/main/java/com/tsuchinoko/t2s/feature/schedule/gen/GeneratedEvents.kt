package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tsuchinoko.t2s.core.designsystem.component.placeholder
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import java.time.LocalDateTime
import java.util.UUID

internal sealed interface GeneratedEventsUiState {
    data object Loading : GeneratedEventsUiState
    data class Generated(val events: List<ScheduleEvent>) : GeneratedEventsUiState
    data class Error(val message: String) : GeneratedEventsUiState
}

@OptIn(ExperimentalMaterial3Api::class)
internal fun LazyListScope.generatedEvents(
    uiState: GeneratedEventsUiState,
    displayType: DisplayType,
    onEventChange: (ScheduleEvent) -> Unit = {},
) {
    when (uiState) {
        GeneratedEventsUiState.Loading -> {
            items(items = skeletonEvents) { event ->
                GeneratedEvent(
                    event = event,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .placeholder(visible = true),
                    onEventChange = onEventChange,
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        is GeneratedEventsUiState.Generated -> {
            val events = uiState.events
            if (displayType == DisplayType.List) {
                items(
                    items = events,
                    key = { it.id },
                ) { event ->
                    GeneratedEvent(
                        event = event,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .placeholder(visible = false),
                        onEventChange = onEventChange,
                    )
                    Spacer(Modifier.height(8.dp))
                }
            } else {
                item {
                    HorizontalUncontainedCarousel(
                        state = rememberCarouselState { events.count() },
                        modifier = Modifier
                            .width(412.dp)
                            .height(221.dp),
                        itemWidth = 186.dp,
                        itemSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp),
                    ) { i ->
                        GeneratedEvent(
                            event = events[i],
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .placeholder(visible = false),
                            onEventChange = onEventChange,
                        )
                    }
                }
            }
        }
        is GeneratedEventsUiState.Error -> {
            item {
                Text(
                    text = uiState.message,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

private val skeletonEvents: List<ScheduleEvent> = run {
    val time = LocalDateTime.parse("2024-08-25T00:00")
    (0..10).toList().map {
        ScheduleEvent(
            id = UUID.randomUUID(),
            title = "",
            memo = null,
            start = time,
            end = time,
        )
    }
}

@Preview
@Composable
fun GeneratedEventsPreview_Generated_List() {
    T2STheme {
        Surface {
            LazyColumn {
                generatedEvents(
                    uiState = GeneratedEventsUiState.Generated(fakeEvents),
                    displayType = DisplayType.List,
                )
            }
        }
    }
}

@Preview
@Composable
fun GeneratedEventsPreview_Generated_Carousel() {
    T2STheme {
        Surface {
            LazyColumn {
                generatedEvents(
                    uiState = GeneratedEventsUiState.Generated(fakeEvents),
                    displayType = DisplayType.Carousel,
                )
            }
        }
    }
}

@Preview
@Composable
fun GeneratedEventsPreview_Error() {
    T2STheme {
        Surface {
            LazyColumn {
                generatedEvents(
                    uiState = GeneratedEventsUiState.Error("予定の生成に失敗しました"),
                    displayType = DisplayType.Carousel,
                )
            }
        }
    }
}
