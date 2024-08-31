package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tsuchinoko.t2s.core.designsystem.component.placeholder
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import com.tsuchinoko.t2s.feature.schedule.R
import java.time.LocalDateTime
import java.util.UUID

@Composable
internal fun ListScheduleGenContent(
    paddingValues: PaddingValues,
    prompt: String,
    generatedEventsUiState: GeneratedEventsUiState,
    onInputEditClick: () -> Unit = {},
    onEventChange: (ScheduleEvent) -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier.padding(paddingValues),
    ) {
        item {
            ScheduleInputCard(
                text = prompt,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp),
                onEditClick = onInputEditClick,
            )

            Spacer(Modifier.height(36.dp))
        }

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

internal val skeletonEvents: List<ScheduleEvent> = run {
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

@Composable
private fun ScheduleInputCard(
    text: String,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit = {},
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = modifier
            .animateContentSize(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(
                onClick = { isExpanded = !isExpanded },
            ) {
                Icon(
                    painter = painterResource(if (isExpanded) R.drawable.collapse else R.drawable.expand),
                    contentDescription = null,
                )

                Spacer(Modifier.width(ButtonDefaults.IconSpacing))

                Text(if (isExpanded) "縮小" else "展開")
            }

            OutlinedButton(
                onClick = onEditClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.edit_text),
                    contentDescription = null,
                )

                Spacer(Modifier.width(ButtonDefaults.IconSpacing))

                Text(text = "編集")
            }
        }

        if (isExpanded) {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        } else {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                maxLines = 3,
            )
        }
    }
}

@Preview
@Composable
fun ListScheduleGenContentPreview_Loading() {
    T2STheme {
        Surface {
            ListScheduleGenContent(
                paddingValues = PaddingValues(0.dp),
                prompt = fakePrompt,
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
                prompt = fakePrompt,
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
                prompt = fakePrompt,
                generatedEventsUiState = GeneratedEventsUiState.Error("予定の生成に失敗しました"),
            )
        }
    }
}
