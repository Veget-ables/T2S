package com.tsuchinoko.t2s.feature.schedule

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun ScheduleGenScreen(
    modifier: Modifier = Modifier,
    scheduleGenViewModel: ScheduleGenViewModel = hiltViewModel(),
) {
    val calendarUiState by scheduleGenViewModel.calendarUiState.collectAsState()
    val scheduleGenUiState by scheduleGenViewModel.scheduleGenUiState.collectAsState()

    ScheduleGenScreen(
        modifier = modifier,
        calendarUiState = calendarUiState,
        scheduleGenUiState = scheduleGenUiState,
        onAccountChange = scheduleGenViewModel::fetchCalendars,
        onConvertClick = scheduleGenViewModel::sendPrompt,
        onEventChange = scheduleGenViewModel::updateInputEvent,
        onRegistryClick = scheduleGenViewModel::registryEvents
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ScheduleGenScreen(
    modifier: Modifier = Modifier,
    calendarUiState: CalendarUiState,
    scheduleGenUiState: ScheduleGenUiState,
    onAccountChange: (accountName: String) -> Unit = {},
    onConvertClick: (prompt: String) -> Unit = {},
    onEventChange:(ScheduleEvent) -> Unit = {},
    onRegistryClick: () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CalendarDrawerSheet(
                uiState = calendarUiState,
                onAccountChange = onAccountChange
            )
        }
    ) {
        Scaffold(
            modifier = modifier.padding(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "title",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.perm_contact_calendar),
                                contentDescription = "Choose Account & Calendar",
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                if (scheduleGenUiState is ScheduleGenUiState.Success) {
                    RegistryButton(onRegistryClick = onRegistryClick)
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .scrollable(
                        state = rememberScrollState(), orientation = Orientation.Vertical
                    ),
            ) {
                var prompt by rememberSaveable { mutableStateOf(TEST_SCHEDULE) }

                TextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    modifier = Modifier
                        .requiredHeight(300.dp)
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                )

                Button(
                    onClick = {
                        onConvertClick(prompt)
                    },
                    enabled = prompt.isNotEmpty(),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 24.dp)
                ) {
                    Text(text = "予定に変換する")
                }

                if (scheduleGenUiState is ScheduleGenUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    if (scheduleGenUiState is ScheduleGenUiState.Error) {
                        Text(
                            text = scheduleGenUiState.errorMessage,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp)
                        )
                    } else if (scheduleGenUiState is ScheduleGenUiState.Success) {
                        LazyColumn(
                            modifier = Modifier
                                .weight(0.8f)
                                .fillMaxSize()
                                .padding(start = 8.dp, top = 16.dp, end = 8.dp),
                            contentPadding = PaddingValues(
                                start = 8.dp,
                                end = 8.dp,
                                top = 16.dp,
                                bottom = 112.dp
                            )
                        ) {
                            items(
                                items = scheduleGenUiState.events,
                                key = { it.id }) { event ->
                                ScheduleEvent(
                                    event = event,
                                    modifier = Modifier.fillMaxWidth(),
                                    onEventChange = onEventChange
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleEvent(
    event: ScheduleEvent,
    modifier: Modifier = Modifier,
    onEventChange: (ScheduleEvent) -> Unit = {}
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = modifier
            .animateContentSize(),
        onClick = { isExpanded = !isExpanded }
    ) {
        if (isExpanded) {
            EditableEventContent(
                event = event,
                modifier = Modifier.fillMaxWidth(),
                onEventChange = onEventChange,
            )
        } else {
            EventContent(event = event)
        }
    }
}

@Composable
private fun EditableEventContent(
    event: ScheduleEvent,
    modifier: Modifier = Modifier,
    onEventChange: (ScheduleEvent) -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 24.dp, vertical = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("終日")
                Switch(
                    checked = event.isAllDay,
                    onCheckedChange = {
                        if (event.isAllDay) {
                            val newStart = event.start.withHour(0).withMinute(0)
                            val new = event.copy(start = newStart, end = newStart)
                            onEventChange(new)
                        } else {
                            val newStart = event.start.withHour(0).withMinute(0)
                            val newEnd = newStart.withHour(23).withMinute(59)
                            val new = event.copy(start = newStart, end = newEnd)
                            onEventChange(new)
                        }
                    }
                )
            }

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ) {
                val start = event.start
                EventDateSelection(
                    localDateTime = start,
                    modifier = Modifier.weight(1f, true),
                    onDateChange = {
                        val newDateTime = it.atTime(start.hour, start.minute)
                        val new = event.copy(start = newDateTime)
                        onEventChange(new)
                    }
                )
                if (event.isAllDay.not()) {
                    EventTimeSelection(
                        localDateTime = start,
                        onTimeChange = { hour, minute ->
                            val newDateTime = start.withHour(hour).withMinute(minute)
                            val new = event.copy(start = newDateTime)
                            onEventChange(new)
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (event.isAllDay.not()) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                ) {
                    val end = event.end
                    EventDateSelection(
                        localDateTime = end,
                        modifier = Modifier.weight(1f, true),
                        onDateChange = {
                            val newDateTime = it.atTime(end.hour, end.minute)
                            val new = event.copy(end = newDateTime)
                            onEventChange(new)
                        }
                    )
                    EventTimeSelection(
                        localDateTime = end,
                        onTimeChange = { hour, minute ->
                            val newDateTime = end.withHour(hour).withMinute(minute)
                            val new = event.copy(end = newDateTime)
                            onEventChange(new)
                        }
                    )
                }
            }

            OutlinedTextField(
                value = event.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                onValueChange = {
                    val new = event.copy(title = it)
                    onEventChange(new)
                },
                label = {
                    Text("予定のタイトル")
                }
            )
            OutlinedTextField(
                value = event.memo ?: "",
                onValueChange = {
                    val new = event.copy(memo = it)
                    onEventChange(new)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                label = {
                    Text("予定のメモ")
                },
                maxLines = 6
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventDateSelection(
    localDateTime: LocalDateTime,
    modifier: Modifier = Modifier,
    onDateChange: (LocalDate) -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Text(
        text = localDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd(E)")),
        modifier = modifier
            .clickable {
                showDatePicker = true
            }
    )
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis =
            localDateTime.atZone(TimeZoneUTC).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val instant = Instant.ofEpochMilli(it)
                            val localDate = instant.atZone(TimeZoneUTC).toLocalDate()
                            onDateChange(localDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("キャンセル")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventTimeSelection(
    localDateTime: LocalDateTime,
    modifier: Modifier = Modifier,
    onTimeChange: (hour: Int, minute: Int) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Text(
        text = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
        modifier = modifier.clickable {
            showTimePicker = true
        }
    )

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    onTimeChange(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            TimePicker(
                state = timePickerState,
            )
        }
    }
}

@Composable
private fun EventContent(event: ScheduleEvent, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier
                .padding(
                    start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp
                )
                .weight(0.9f)
        ) {
            Text(text = event.displayDateTime.value)
            Text(text = event.title)
        }
        if (event.memo != null) {
            Icon(
                painter = painterResource(R.drawable.memo),
                contentDescription = "memo",
                modifier = Modifier
                    .weight(0.1f)
            )
        } else {
            Spacer(Modifier.weight(0.1f))
        }
    }
}

@Composable
private fun RegistryButton(modifier: Modifier = Modifier, onRegistryClick: () -> Unit = {}) {
    FloatingActionButton(
        modifier = modifier
            .padding(vertical = 16.dp),
        onClick = onRegistryClick,
    ) {
        Icon(
            painter = painterResource(R.drawable.event_upcoming),
            contentDescription = "カレンダーに登録"
        )
    }
}

private val TimeZoneUTC = ZoneId.of("UTC")

@Preview
@Composable
fun ScheduleGenScreenPreview() {
    T2STheme {
        val events = listOf(
            createScheduleEvent(
                title = "終日予定",
                memo = "これはメモです",
                start = LocalDateTime.parse("2020-02-15T00:00"),
                end = LocalDateTime.parse("2020-02-15T23:59")
            ),
            createScheduleEvent(
                title = "一日予定",
                memo = "これはメモです",
                start = LocalDateTime.parse("2020-02-15T01:30"),
                end = LocalDateTime.parse("2020-02-15T23:30")
            ),
            createScheduleEvent(
                title = "日をまたぐ予定",
                start = LocalDateTime.parse("2020-02-15T21:30"),
                end = LocalDateTime.parse("2020-02-16T21:30")
            ),
            createScheduleEvent(
                title = "タイトルがとても長くて2行以上になってしまう予定",
                start = LocalDateTime.parse("2020-02-15T21:30"),
                end = LocalDateTime.parse("2020-02-16T21:30")
            )
        )
        ScheduleGenScreen(
            calendarUiState = CalendarUiState.Initial,
            scheduleGenUiState = ScheduleGenUiState.Success(events)
        )
    }
}

@Preview
@Composable
fun EditableEventContentPreview() {
    T2STheme {
        Column {
            Card {
                EditableEventContent(
                    event = createScheduleEvent(
                        title = "終日予定",
                        memo = "これはメモです",
                        start = LocalDateTime.parse("2020-02-15T00:00"),
                        end = LocalDateTime.parse("2020-02-15T23:59")
                    )
                )
            }

            Spacer(Modifier.height(16.dp))

            Card {
                EditableEventContent(
                    event = createScheduleEvent(
                        title = "タイトルがとても長くて2行以上になってしまう予定",
                        memo = "11:00 入り \n12:00 - 13:00 解散",
                        start = LocalDateTime.parse("2020-02-15T21:30"),
                        end = LocalDateTime.parse("2020-02-16T21:30")
                    )
                )
            }

            Spacer(Modifier.height(16.dp))

            Card {
                EditableEventContent(
                    event = createScheduleEvent(
                        title = "予定",
                        memo = "6行以上にまたがるように作るられたメモ。これは6行にまたがってもレイアウトが壊れなければOK。6行以上にまたがるように作るられたメモ。これは6行にまたがってもレイアウトが壊れなければOK。6行以上にまたがるように作るられたメモ。これは6行にまたがってもレイアウトが壊れなければOK。6行以上にまたがるように作るられたメモ。これは6行にまたがってもレイアウトが壊れなければOK。",
                        start = LocalDateTime.parse("2020-02-15T21:30"),
                        end = LocalDateTime.parse("2020-02-16T21:30")
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun EventContentPreview() {
    T2STheme {
        Card {
            EventContent(
                event = createScheduleEvent(
                    title = "日をまたぐ予定",
                    start = LocalDateTime.parse("2020-02-15T21:30"),
                    end = LocalDateTime.parse("2020-02-16T21:30")
                )
            )
        }
    }
}

fun createScheduleEvent(
    id: UUID = UUID.randomUUID(),
    title: String,
    memo: String? = null,
    start: LocalDateTime,
    end: LocalDateTime
): ScheduleEvent {
    return ScheduleEvent(id = id, title = title, memo = memo, start = start, end = end)
}
