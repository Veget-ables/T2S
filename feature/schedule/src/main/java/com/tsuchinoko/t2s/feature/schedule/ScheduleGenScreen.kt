package com.tsuchinoko.t2s.feature.schedule

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.CalendarId
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountSelection
import com.tsuchinoko.t2s.feature.schedule.account.CalendarUiState
import com.tsuchinoko.t2s.feature.schedule.guide.TextInputGuideScreen
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
    var prompt by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                CalendarAccountSelection(
                    uiState = scheduleGenUiState.calendarUiState,
                    onAccountChange = onAccountChange,
                    onCalendarChange = onTargetCalendarChange,
                )
            }
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
                        when (scheduleGenUiState.generatedEventsUiState) {
                            GeneratedEventsUiState.Empty -> {
                                FloatingActionButton(
                                    onClick = { onConvertClick(prompt) },
                                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.convert_to_event),
                                        contentDescription = "予定オブジェクトに変換する",
                                    )
                                }
                            }

                            GeneratedEventsUiState.Loading -> {
                            }

                            is GeneratedEventsUiState.Generated -> {
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
                            }

                            is GeneratedEventsUiState.Error -> {
                            }
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
            )
        }
    }
}

enum class InputStatus {
    Idling,
    Input,
}

@Composable
private fun ScheduleGenContent(
    modifier: Modifier = Modifier,
    uiState: GeneratedEventsUiState,
    onEventChange: (ScheduleEvent) -> Unit = {},
) {
    var inputStatus by rememberSaveable { mutableStateOf(InputStatus.Idling) }
    var prompt by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = modifier,
    ) {
        if (inputStatus == InputStatus.Input) {
            OutlinedTextField(value = prompt, onValueChange = { text -> prompt = text })
        }
        when (uiState) {
            GeneratedEventsUiState.Empty -> {
                TextInputGuideScreen(
                    modifier = Modifier.fillMaxSize(),
                    onInputClick = {
                        prompt = it
                        inputStatus = InputStatus.Input
                    },
                )
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

@Preview
@Composable
fun CalendarAccountDrawerPreview_Initial() {
    T2STheme {
        ModalNavigationDrawer(
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
            drawerContent = {
                ModalDrawerSheet {
                    CalendarAccountSelection(
                        uiState = CalendarUiState.Initial,
                    )
                }
            },
        ) {}
    }
}

@Preview
@Composable
fun CalendarAccountDrawerPreview_AccountSelected() {
    T2STheme {
        val calendar1 = Calendar(id = CalendarId("1"), title = "calendar1")
        val calendar2 = Calendar(id = CalendarId("2"), title = "calendar2")
        val calendar3 = Calendar(id = CalendarId("3"), title = "calendar3")

        ModalNavigationDrawer(
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
            drawerContent = {
                ModalDrawerSheet {
                    CalendarAccountSelection(
                        uiState = CalendarUiState.AccountSelected(
                            accountName = "taro",
                            calendars = listOf(calendar1, calendar2, calendar3),
                            targetCalendar = calendar1,
                        ),
                    )
                }
            },
        ) {}
    }
}

@Preview
@Composable
fun CalendarAccountDrawerPreview_Error() {
    T2STheme {
        ModalNavigationDrawer(
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
            drawerContent = {
                ModalDrawerSheet {
                    CalendarAccountSelection(
                        uiState = CalendarUiState.Error("アカウントの取得に失敗しました"),
                    )
                }
            },
        ) {}
    }
}
