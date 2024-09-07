package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tsuchinoko.t2s.core.designsystem.component.placeholder
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.core.model.BaseInput
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import com.tsuchinoko.t2s.feature.schedule.R

@Composable
internal fun CarouselScheduleGenContent(
    scheduleInput: String,
    generatedEventsUiState: GeneratedEventsUiState,
    modifier: Modifier = Modifier,
    onEditClick: (ScheduleEvent) -> Unit = {},
    onRegistryClick: (ScheduleEvent) -> Unit = {},
    onDeleteClick: (ScheduleEvent) -> Unit = {},
) {
    Column(modifier = modifier) {
        var baseInput: BaseInput? by remember(scheduleInput) { mutableStateOf(null) }
        val textFieldValue by rememberHighlightTextValue(
            scheduleInput = scheduleInput,
            baseInput = baseInput,
        )
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .weight(0.5f),
        )

        when (generatedEventsUiState) {
            GeneratedEventsUiState.Loading -> {
                CarouselScheduleEvents(
                    events = skeletonEvents,
                    placeholder = true,
                    modifier = Modifier.weight(0.5f),
                )
            }

            is GeneratedEventsUiState.Generated -> {
                CarouselScheduleEvents(
                    events = generatedEventsUiState.events,
                    placeholder = false,
                    modifier = Modifier.weight(0.5f),
                    onTargetClick = { baseInput = it },
                    onEditClick = onEditClick,
                    onRegistryClick = onRegistryClick,
                    onDeleteClick = onDeleteClick,
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
    onTargetClick: (BaseInput) -> Unit = {},
    onEditClick: (ScheduleEvent) -> Unit = {},
    onRegistryClick: (ScheduleEvent) -> Unit = {},
    onDeleteClick: (ScheduleEvent) -> Unit = {},
) {
    val carouselState = remember(events.size) {
        CarouselState { events.count() }
    }
    HorizontalMultiBrowseCarousel(
        state = carouselState,
        preferredItemWidth = 600.dp,
        modifier = modifier,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) { i ->
        GeneratedEventCard(
            event = events[i],
            modifier = Modifier
                .fillMaxWidth()
                .maskClip(CardDefaults.shape)
                .padding(start = if (i == 0 || events.lastIndex == i) 0.dp else 16.dp)
                .placeholder(visible = placeholder),
            onTargetClick = onTargetClick,
            onEditClick = onEditClick,
            onRegistryClick = onRegistryClick,
            onDeleteClick = onDeleteClick,
        )
    }
}

@Composable
private fun GeneratedEventCard(
    event: ScheduleEvent,
    modifier: Modifier = Modifier,
    onTargetClick: (BaseInput) -> Unit = {},
    onEditClick: (ScheduleEvent) -> Unit = {},
    onRegistryClick: (ScheduleEvent) -> Unit = {},
    onDeleteClick: (ScheduleEvent) -> Unit = {},
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 16.dp,
            ),
        ) {
            Row {
                IconButton(onClick = { onTargetClick(event.base) }) {
                    Icon(
                        painter = painterResource(R.drawable.location_search),
                        contentDescription = "Location",
                    )
                }

                Spacer(Modifier.weight(1f))

                IconButton(onClick = { onEditClick(event) }) {
                    Icon(
                        painter = painterResource(R.drawable.edit),
                        contentDescription = "Edit",
                    )
                }

                var expanded by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopStart),
                ) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "メニュー")
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        val clipboardManager: ClipboardManager = LocalClipboardManager.current
                        DropdownMenuItem(
                            text = { Text("コピー") },
                            onClick = {
                                clipboardManager.setText(AnnotatedString(event.copiedText))
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.copy),
                                    contentDescription = null,
                                )
                            },
                        )

                        DropdownMenuItem(
                            text = { Text("カレンダーに登録") },
                            onClick = {
                                onRegistryClick(event)
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.event_upcoming),
                                    contentDescription = null,
                                )
                            },
                        )
                        HorizontalDivider()

                        DropdownMenuItem(
                            text = { Text("削除") },
                            onClick = {
                                onDeleteClick(event)
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.delete),
                                    contentDescription = null,
                                )
                            },
                        )
                    }
                }
            }

            Text(
                text = event.title,
                modifier = Modifier.padding(top = 4.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = MaterialTheme.typography.titleLarge,
            )

            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.schedule_datetime),
                    contentDescription = "",
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    text = event.displayDateTime.value,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = event.memo,
                overflow = TextOverflow.Ellipsis,
                maxLines = 4,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

private val skeletonEvents: List<ScheduleEvent> = run {
    (0..10).toList().map {
        fakeLongTitleAndMemoEvent
    }
}

@Preview
@Composable
fun CarouselScheduleGenContentPreview_Loading() {
    T2STheme {
        Surface {
            CarouselScheduleGenContent(
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
                scheduleInput = fakeScheduleInput,
                generatedEventsUiState = GeneratedEventsUiState.Error("予定の生成に失敗しました"),
            )
        }
    }
}
