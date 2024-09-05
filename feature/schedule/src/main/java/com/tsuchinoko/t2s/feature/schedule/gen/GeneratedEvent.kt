package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val TimeZoneUTC = ZoneId.of("UTC")

@Composable
internal fun EditableEventContent(
    event: ScheduleEvent,
    modifier: Modifier = Modifier,
    onEventChange: (ScheduleEvent) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 24.dp,
                vertical = 16.dp,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = event.title,
            modifier = Modifier
                .fillMaxWidth(),
            textStyle = TextStyle(fontSize = MaterialTheme.typography.titleLarge.fontSize),
            onValueChange = {
                val new = event.copy(title = it)
                onEventChange(new)
            },
            maxLines = 3,
            label = {
                Text("予定のタイトル")
            },
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
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
                },
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        ) {
            val start = event.start
            EventDateSelection(
                localDateTime = start,
                modifier = Modifier.weight(1f, true),
                onDateChange = {
                    val newDateTime = it.atTime(start.hour, start.minute)
                    val new = event.copy(start = newDateTime)
                    onEventChange(new)
                },
            )
            if (event.isAllDay.not()) {
                EventTimeSelection(
                    localDateTime = start,
                    onTimeChange = { hour, minute ->
                        val newDateTime = start.withHour(hour).withMinute(minute)
                        val new = event.copy(start = newDateTime)
                        onEventChange(new)
                    },
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        if (event.isAllDay.not()) {
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            ) {
                val end = event.end
                EventDateSelection(
                    localDateTime = end,
                    modifier = Modifier.weight(1f, true),
                    onDateChange = {
                        val newDateTime = it.atTime(end.hour, end.minute)
                        val new = event.copy(end = newDateTime)
                        onEventChange(new)
                    },
                )
                EventTimeSelection(
                    localDateTime = end,
                    onTimeChange = { hour, minute ->
                        val newDateTime = end.withHour(hour).withMinute(minute)
                        val new = event.copy(end = newDateTime)
                        onEventChange(new)
                    },
                )
            }
        }

        OutlinedTextField(
            value = event.memo,
            onValueChange = {
                val new = event.copy(memo = it)
                onEventChange(new)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            label = {
                Text("予定のメモ")
            },
            maxLines = 5,
        )
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
            },
    )
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis =
            localDateTime.atZone(TimeZoneUTC).toInstant().toEpochMilli(),
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
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("キャンセル")
                }
            },
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
    onTimeChange: (hour: Int, minute: Int) -> Unit,
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Text(
        text = localDateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
        modifier = modifier.clickable {
            showTimePicker = true
        },
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
            },
        ) {
            TimePicker(
                state = timePickerState,
            )
        }
    }
}

@Composable
private fun EventContent(event: ScheduleEvent, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(text = event.displayDateTime.value)
        Text(text = event.title)
    }
}

@Preview
@Composable
fun EditableEventContentPreview() {
    T2STheme {
        Column {
            Card {
                EditableEventContent(event = fakeAllDayEvent)
            }

            Spacer(Modifier.height(16.dp))

            Card {
                EditableEventContent(event = fakeLongTitleEvent)
            }

            Spacer(Modifier.height(16.dp))

            Card {
                EditableEventContent(event = fakeLongMemoEvent)
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
                event = fake2DaysEvent,
            )
        }
    }
}
