package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

@Composable
internal fun ListScheduleGenContent(
    paddingValues: PaddingValues,
    prompt: String,
    generatedEventsUiState: GeneratedEventsUiState,
    onEventChange: (ScheduleEvent) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
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

        LazyColumn(
            modifier = Modifier
                .weight(1f),
        ) {
            when (generatedEventsUiState) {
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
                    val events = generatedEventsUiState.events
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
                            onEventClick = {},
                            onEventChange = onEventChange,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                is GeneratedEventsUiState.Error -> {
                    item {
                        Text(
                            text = generatedEventsUiState.message,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }
        }
    }
}

internal val skeletonEvents: List<ScheduleEvent> = run {
    val time = LocalDateTime.parse("2024-08-25T00:00")
    (0..10).toList().map {
        ScheduleEvent(
            id = UUID.randomUUID(),
            title = "",
            memo = "",
            start = time,
            end = time,
            base = "",
        )
    }
}

@Preview
@Composable
fun ListScheduleGenContentPreview_Loading() {
    T2STheme {
        Surface {
            ListScheduleGenContent(
                paddingValues = PaddingValues(0.dp),
                prompt = fakeScheduleInput,
                generatedEventsUiState = GeneratedEventsUiState.Loading,
            )
        }
    }
}

@Preview
@Composable
fun ListScheduleGenContentPreview_Generated_List() {
    T2STheme {
        Surface {
            ListScheduleGenContent(
                paddingValues = PaddingValues(0.dp),
                prompt = fakeScheduleInput,
                generatedEventsUiState = GeneratedEventsUiState.Generated(fakeEvents),
            )
        }
    }
}

@Preview
@Composable
fun ListScheduleGenContentPreview_Error() {
    T2STheme {
        Surface {
            ListScheduleGenContent(
                paddingValues = PaddingValues(0.dp),
                prompt = fakeScheduleInput,
                generatedEventsUiState = GeneratedEventsUiState.Error("予定の生成に失敗しました"),
            )
        }
    }
}
