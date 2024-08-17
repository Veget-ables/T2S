package com.tsuchinoko.t2s.feature.schedule

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import kotlinx.coroutines.launch

@Composable
internal fun ScheduleGenScreen(
    modifier: Modifier = Modifier,
    scheduleGenViewModel: ScheduleGenViewModel = hiltViewModel(),
) {
    val scheduleGenUiState by scheduleGenViewModel.scheduleGenUiState.collectAsState()

    ScheduleGenScreen(
        modifier = modifier,
        scheduleGenUiState = scheduleGenUiState,
        onAccountChange = scheduleGenViewModel::fetchCalendars,
        onTargetCalendarChange = scheduleGenViewModel::updateTargetCalendar,
        onConvertClick = scheduleGenViewModel::sendPrompt,
        onEventChange = scheduleGenViewModel::updateInputEvent,
        onRegistryClick = scheduleGenViewModel::registryEvents,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ScheduleGenScreen(
    modifier: Modifier = Modifier,
    scheduleGenUiState: ScheduleGenUiState,
    onAccountChange: (accountName: String) -> Unit = {},
    onTargetCalendarChange: (calendar: Calendar) -> Unit = {},
    onConvertClick: (prompt: String) -> Unit = {},
    onEventChange: (ScheduleEvent) -> Unit = {},
    onRegistryClick: () -> Unit = {},
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            CalendarDrawerSheet(
                uiState = scheduleGenUiState.calendarUiState,
                onAccountChange = onAccountChange,
                onCalendarChange = onTargetCalendarChange,
            )
        },
    ) {
        Scaffold(
            modifier = modifier.padding(),
            topBar = {
            },
            bottomBar = {
                BottomAppBar(
                    actions = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.perm_contact_calendar),
                                contentDescription = "Choose Account & Calendar",
                            )
                        }
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.open),
                                contentDescription = "Choose Account & Calendar",
                            )
                        }
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = onRegistryClick,
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.event_upcoming),
                                contentDescription = "カレンダーに登録",
                            )
                        }
                    },
                )
            },
        ) { paddingValues ->
            ScheduleGenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .scrollable(
                        state = rememberScrollState(),
                        orientation = Orientation.Vertical,
                    ),
                uiState = scheduleGenUiState.generatedEventsUiState,
                onConvertClick = onConvertClick,
            )
        }
    }
}

@Composable
private fun ScheduleGenContent(
    modifier: Modifier = Modifier,
    uiState: GeneratedEventsUiState,
    onConvertClick: (prompt: String) -> Unit = {},
    onEventChange: (ScheduleEvent) -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        var prompt by rememberSaveable { mutableStateOf("") }

        when (uiState) {
            GeneratedEventsUiState.Empty -> {
                TextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    placeholder = {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text("予定に変換するテキスト入力")

                            Text("または", style = MaterialTheme.typography.labelSmall)

                            TextButton(onClick = {}) {
                                Icon(
                                    painter = painterResource(R.drawable.open),
                                    contentDescription = null,
                                )

                                Spacer(Modifier.width(ButtonDefaults.IconSpacing))

                                Text(text = "テキストをインポート")
                            }
                        }
                    },
                )
                Button(
                    onClick = {
                        onConvertClick(prompt)
                    },
                    enabled = prompt.isNotEmpty(),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 24.dp),
                ) {
                    Text(text = "予定に変換する")
                }
            }

            GeneratedEventsUiState.Loading -> {
            }

            is GeneratedEventsUiState.Generated -> {
                GeneratedEvents(
                    uiState = uiState,
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxSize()
                        .padding(start = 8.dp, top = 16.dp, end = 8.dp),
                    onEventChange = onEventChange,
                )
            }

            is GeneratedEventsUiState.Error -> {
            }
        }
    }
}

@Preview
@Composable
fun ScheduleGenScreenPreview() {
    T2STheme {
        ScheduleGenScreen(
            scheduleGenUiState = ScheduleGenUiState.Empty,
        )
    }
}
