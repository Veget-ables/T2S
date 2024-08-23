package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.CalendarId
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import com.tsuchinoko.t2s.feature.schedule.R
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountSelection
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountUiState
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountViewModel
import com.tsuchinoko.t2s.feature.schedule.account.fakeUiStateAccountSelected
import kotlinx.coroutines.launch

@Composable
internal fun ScheduleGenScreen(
    modifier: Modifier = Modifier,
    calendarAccountViewModel: CalendarAccountViewModel,
    scheduleGenViewModel: ScheduleGenViewModel = hiltViewModel(),
) {
    val calendarAccountUiState by calendarAccountViewModel.calendarAccountUiState.collectAsState()
    val scheduleGenUiState by scheduleGenViewModel.scheduleGenUiState.collectAsState()

    ScheduleGenScreen(
        modifier = modifier,
        calendarAccountUiState = calendarAccountUiState,
        scheduleGenUiState = scheduleGenUiState,
        onAccountChange = calendarAccountViewModel::fetchCalendars,
        onTargetCalendarChange = calendarAccountViewModel::updateTargetCalendar,
        onEventChange = scheduleGenViewModel::updateInputEvent,
        onRegistryClick = scheduleGenViewModel::registryEvents,
    )
}

@Composable
private fun ScheduleGenScreen(
    modifier: Modifier = Modifier,
    calendarAccountUiState: CalendarAccountUiState,
    scheduleGenUiState: ScheduleGenUiState,
    onAccountChange: (account: Account) -> Unit = {},
    onTargetCalendarChange: (calendar: Calendar) -> Unit = {},
    onEventChange: (ScheduleEvent) -> Unit = {},
    onRegistryClick: (calendarId: CalendarId, events: List<ScheduleEvent>) -> Unit = { _, _ -> },
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val generatedEventsUiState = scheduleGenUiState.generatedEventsUiState
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                CalendarAccountSelection(
                    uiState = calendarAccountUiState,
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
                                contentDescription = "アカウントを設定",
                            )
                        }
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.edit_text),
                                contentDescription = "テキストを入力",
                            )
                        }
                    },
                    floatingActionButton = {
                        if (generatedEventsUiState is GeneratedEventsUiState.Generated) {
                            FloatingActionButton(
                                onClick = {
                                    if (calendarAccountUiState is CalendarAccountUiState.AccountSelected) {
                                        onRegistryClick(
                                            calendarAccountUiState.targetCalendar.id,
                                            generatedEventsUiState.events,
                                        )
                                    }
                                },
                                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.event_upcoming),
                                    contentDescription = "カレンダーに登録",
                                )
                            }
                        }
                    },
                )
            },
        ) { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 24.dp, end = 24.dp),
                ) {
                    Text(
                        text = scheduleGenUiState.prompt,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                    )
                }

                ScheduleGenContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .scrollable(
                            state = rememberScrollState(),
                            orientation = Orientation.Vertical,
                        ),
                    uiState = generatedEventsUiState,
                    onEventChange = onEventChange,
                )
            }
        }
    }
}

@Composable
private fun ScheduleGenContent(
    modifier: Modifier = Modifier,
    uiState: GeneratedEventsUiState,
    onEventChange: (ScheduleEvent) -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        when (uiState) {
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
fun ScheduleGenScreenPreview_Initial() {
    T2STheme {
        ScheduleGenScreen(
            calendarAccountUiState = CalendarAccountUiState.Initial,
            scheduleGenUiState = ScheduleGenUiState.Initial,
        )
    }
}

@Preview
@Composable
fun ScheduleGenScreenPreview_Generated() {
    T2STheme {
        ScheduleGenScreen(
            calendarAccountUiState = CalendarAccountUiState.Initial,
            scheduleGenUiState = ScheduleGenUiState(
                prompt = "2020年2月15日1:30〜25日23:30　通常予定\n これはメモです\n ",
                generatedEventsUiState = GeneratedEventsUiState.Generated(fakeEvents),
            ),
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
                        uiState = CalendarAccountUiState.Initial,
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
        ModalNavigationDrawer(
            drawerState = rememberDrawerState(initialValue = DrawerValue.Open),
            drawerContent = {
                ModalDrawerSheet {
                    CalendarAccountSelection(uiState = fakeUiStateAccountSelected)
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
                        uiState = CalendarAccountUiState.Error("アカウントの取得に失敗しました"),
                    )
                }
            },
        ) {}
    }
}
