package com.tsuchinoko.t2s.feature.schedule.gen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    onInputEditClick: () -> Unit = {},
) {
    val calendarAccountUiState by calendarAccountViewModel.calendarAccountUiState.collectAsState()
    val scheduleGenUiState by scheduleGenViewModel.scheduleGenUiState.collectAsState()
    val registryResultUiState by scheduleGenViewModel.registryResultUiState.collectAsState()

    ScheduleGenScreen(
        modifier = modifier,
        calendarAccountUiState = calendarAccountUiState,
        scheduleGenUiState = scheduleGenUiState,
        registryResultUiState = registryResultUiState,
        onAccountChange = calendarAccountViewModel::updateAccount,
        onTargetCalendarChange = calendarAccountViewModel::updateTargetCalendar,
        onInputEditClick = onInputEditClick,
        onEventChange = scheduleGenViewModel::updateInputEvent,
        onRegistryClick = scheduleGenViewModel::registryEvents,
        onDisplayTypeChange = scheduleGenViewModel::updateDisplayType,
    )
}

@Composable
private fun ScheduleGenScreen(
    modifier: Modifier = Modifier,
    calendarAccountUiState: CalendarAccountUiState,
    scheduleGenUiState: ScheduleGenUiState,
    registryResultUiState: RegistryResultUiState,
    onAccountChange: (account: Account) -> Unit = {},
    onTargetCalendarChange: (calendar: Calendar) -> Unit = {},
    onDisplayTypeChange: (type: DisplayType) -> Unit = {},
    onInputEditClick: () -> Unit = {},
    onEventChange: (ScheduleEvent) -> Unit = {},
    onRegistryClick: (calendarId: CalendarId, events: List<ScheduleEvent>) -> Unit = { _, _ -> },
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val generatedEventsUiState = scheduleGenUiState.generatedEventsUiState

    val snackbarHostState = remember { SnackbarHostState() }
    when (registryResultUiState) {
        RegistryResultUiState.Standby,
        RegistryResultUiState.Loading,
        -> {
        }

        RegistryResultUiState.Success -> {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "カレンダーに予定を登録しました",
                    actionLabel = "開く",
                )
                if (result == SnackbarResult.ActionPerformed) {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        val data = Uri.parse("content://com.android.calendar/time")
                        setData(data)
                    }
                    context.startActivity(intent)
                }
            }
        }

        is RegistryResultUiState.Error -> {
            scope.launch {
                snackbarHostState.showSnackbar("予定の登録に失敗しました")
            }
        }
    }

    ModalNavigationDrawer(
        modifier = modifier,
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
                                if (scheduleGenUiState.displayType == DisplayType.List) {
                                    onDisplayTypeChange(DisplayType.Carousel)
                                } else {
                                    onDisplayTypeChange(DisplayType.List)
                                }
                            },
                        ) {
                            val drawableId =
                                if (scheduleGenUiState.displayType == DisplayType.List) R.drawable.carousel else R.drawable.list
                            Icon(
                                painter = painterResource(drawableId),
                                contentDescription = "アカウントを設定",
                            )
                        }
                    },
                    floatingActionButton = {
                        when {
                            generatedEventsUiState is GeneratedEventsUiState.Loading || registryResultUiState is RegistryResultUiState.Loading -> {
                                FloatingActionButton(
                                    onClick = {},
                                    containerColor = Color.Transparent,
                                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        strokeWidth = 3.dp,
                                    )
                                }
                            }

                            generatedEventsUiState is GeneratedEventsUiState.Generated -> {
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

                            else -> {}
                        }
                    },
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { paddingValues ->
            when (scheduleGenUiState.displayType) {
                DisplayType.List -> ListScheduleGenContent(
                    paddingValues = paddingValues,
                    prompt = scheduleGenUiState.prompt,
                    generatedEventsUiState = generatedEventsUiState,
                    onInputEditClick = onInputEditClick,
                    onEventChange = onEventChange,
                )

                DisplayType.Carousel -> CarouselScheduleGenContent(
                    paddingValues = paddingValues,
                    prompt = scheduleGenUiState.prompt,
                    generatedEventsUiState = generatedEventsUiState,
                    onInputEditClick = onInputEditClick,
                    onEventChange = onEventChange,
                )
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
            registryResultUiState = RegistryResultUiState.Standby,
        )
    }
}

@Preview
@Composable
fun ScheduleGenScreenPreview_Generated_List() {
    T2STheme {
        ScheduleGenScreen(
            calendarAccountUiState = CalendarAccountUiState.Initial,
            scheduleGenUiState = ScheduleGenUiState(
                prompt = "2020年2月15日1:30〜25日23:30　通常予定\n これはメモです\n ",
                generatedEventsUiState = GeneratedEventsUiState.Generated(fakeEvents),
                displayType = DisplayType.List,
            ),
            registryResultUiState = RegistryResultUiState.Standby,
        )
    }
}

@Preview
@Composable
fun ScheduleGenScreenPreview_Generated_Carousel() {
    T2STheme {
        ScheduleGenScreen(
            calendarAccountUiState = CalendarAccountUiState.Initial,
            scheduleGenUiState = ScheduleGenUiState(
                prompt = "2020年2月15日1:30〜25日23:30　通常予定\n これはメモです\n ",
                generatedEventsUiState = GeneratedEventsUiState.Generated(fakeEvents),
                displayType = DisplayType.Carousel,
            ),
            registryResultUiState = RegistryResultUiState.Standby,
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
