package com.tsuchinoko.t2s

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tsuchinoko.t2s.ui.theme.T2STheme
import java.time.LocalDateTime

@Composable
fun ScheduleGenScreen(
    modifier: Modifier = Modifier,
    scheduleGenViewModel: ScheduleGenViewModel = viewModel()
) {
    val uiState by scheduleGenViewModel.uiState.collectAsState()

    ScheduleGenScreen(
        modifier = modifier,
        uiState = uiState,
        onClickConvert = scheduleGenViewModel::sendPrompt
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ScheduleGenScreen(
    modifier: Modifier = Modifier,
    uiState: UiState,
    onClickConvert: (prompt: String) -> Unit = {}
) {
    Scaffold(
        modifier = modifier.padding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            )
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
                label = { Text(stringResource(R.string.label_prompt)) },
                onValueChange = { prompt = it },
                modifier = Modifier
                    .requiredHeight(300.dp)
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
            )

            Button(
                onClick = {
                    onClickConvert(prompt)
                },
                enabled = prompt.isNotEmpty(),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 24.dp)
            ) {
                Text(text = "予定に変換する")
            }

            if (uiState is UiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                if (uiState is UiState.Error) {
                    Text(
                        text = uiState.errorMessage,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(16.dp)
                    )

                } else if (uiState is UiState.Success) {
                    Column {
                        ScheduleEvents(
                            scheduleEvents = uiState.scheduleEvents,
                            modifier = Modifier
                                .weight(0.8f)
                                .fillMaxSize()
                                .padding(start = 8.dp, top = 16.dp, end = 8.dp),
                        )
                        RegistryButton(
                            modifier = Modifier
                                .weight(0.2f)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleEvents(scheduleEvents: List<ScheduleEvent>, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceTint,
        shape = RoundedCornerShape(8.dp)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp)
        ) {
            items(scheduleEvents) { event ->
                ScheduleEvent(
                    event = event,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun ScheduleEvent(event: ScheduleEvent, modifier: Modifier = Modifier) {
    var isExpanded by remember { mutableStateOf(false) }
    var _event by remember { mutableStateOf(event) }
    Card(
        modifier = modifier
            .animateContentSize(),
        onClick = { isExpanded = !isExpanded }
    ) {
        if (isExpanded) {
            EditableEventContent(
                event = _event,
                modifier = Modifier.fillMaxWidth(),
                onEventChange = { _event = it },
            )
        } else {
            EventContent(
                event = _event,
            )
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
                    horizontal = 24.dp,
                    vertical = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EventDateTime(
                date = event.simpleStartDate,
                time = event.simpleStartTime
            )
            EventDateTime(
                date = event.simpleEndDate,
                time = event.simpleEndTime,
                modifier = Modifier.padding(top = 8.dp)
            )

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
private fun EventDateTime(date: String, time: String, modifier: Modifier = Modifier) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Absolute.SpaceBetween
    ) {
        Text(
            text = date,
            modifier = Modifier
                .weight(1f, true)
                .clickable {
                    showDatePicker = true
                }
        )
        Text(
            text = time,
            modifier = Modifier.clickable {
                showTimePicker = true
            }
        )
    }
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
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
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@ExperimentalMaterial3Api
@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable ColumnScope.() -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.wrapContentHeight(),
        properties = properties
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween) {
            content()
            // Buttons
        }
    }
}

@Composable
private fun EventContent(event: ScheduleEvent, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    end = 4.dp,
                    top = 4.dp,
                    bottom = 4.dp
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
private fun RegistryButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Button(
        modifier = modifier
            .padding(vertical = 16.dp),
        onClick = {
            val intent = Intent(Intent.ACTION_INSERT)
            intent.setData(CalendarContract.Events.CONTENT_URI)
            val startTimeMillis = System.currentTimeMillis() + 10 * 60 * 1000
            intent.putExtra(CalendarContract.Events.TITLE, "予定")
            intent.putExtra(CalendarContract.Events.DTSTART, startTimeMillis)
            intent.putExtra(
                CalendarContract.Events.DTEND,
                startTimeMillis + 30 * 60 * 1000
            )
            context.startActivity(intent)
        },
    ) {
        Text("Google Calendarに登録")
    }
}

@Preview
@Composable
fun ScheduleGenScreenPreview() {
    T2STheme {
        val events = listOf(
            ScheduleEvent(
                title = "終日予定",
                memo = "これはメモです",
                start = LocalDateTime.parse("2020-02-15T00:00"),
                end = LocalDateTime.parse("2020-02-15T23:59")
            ),
            ScheduleEvent(
                title = "一日予定",
                memo = "これはメモです",
                start = LocalDateTime.parse("2020-02-15T01:30"),
                end = LocalDateTime.parse("2020-02-15T23:30")
            ),
            ScheduleEvent(
                title = "日をまたぐ予定",
                memo = null,
                start = LocalDateTime.parse("2020-02-15T21:30"),
                end = LocalDateTime.parse("2020-02-16T21:30")
            ),
            ScheduleEvent(
                title = "タイトルがとても長くて2行以上になってしまう予定",
                memo = null,
                start = LocalDateTime.parse("2020-02-15T21:30"),
                end = LocalDateTime.parse("2020-02-16T21:30")
            )
        )
        ScheduleGenScreen(uiState = UiState.Success(events))
    }
}

@Preview
@Composable
fun EditableEventContentPreview() {
    T2STheme {
        Column {
            Card {
                EditableEventContent(
                    event = ScheduleEvent(
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
                    event = ScheduleEvent(
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
                    event = ScheduleEvent(
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
                event = ScheduleEvent(
                    title = "日をまたぐ予定",
                    memo = null,
                    start = LocalDateTime.parse("2020-02-15T21:30"),
                    end = LocalDateTime.parse("2020-02-16T21:30")
                )
            )
        }
    }
}
