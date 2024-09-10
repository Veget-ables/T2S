package com.tsuchinoko.t2s.feature.schedule.gen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.CalendarId
import com.tsuchinoko.t2s.core.model.ScheduleEvent
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
    onBackClick: () -> Unit = {},
) {
    val calendarAccountUiState by calendarAccountViewModel.calendarAccountUiState.collectAsState()
    val scheduleGenUiState by scheduleGenViewModel.scheduleGenUiState.collectAsState()
    val registryResultUiState by scheduleGenViewModel.registryResultUiState.collectAsState()

    ScheduleGenScreen(
        modifier = modifier,
        calendarAccountUiState = calendarAccountUiState,
        scheduleGenUiState = scheduleGenUiState,
        registryResultUiState = registryResultUiState,
        onBackClick = onBackClick,
        onAccountChange = calendarAccountViewModel::updateAccount,
        onTargetCalendarChange = calendarAccountViewModel::updateTargetCalendar,
        onRefreshClick = scheduleGenViewModel::generateEvents,
        onAddClick = scheduleGenViewModel::addEvent,
        onEventChange = scheduleGenViewModel::updateInputEvent,
        onDeleteClick = scheduleGenViewModel::deleteEvent,
        onRegistryClick = scheduleGenViewModel::registryEvents,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleGenScreen(
    modifier: Modifier = Modifier,
    calendarAccountUiState: CalendarAccountUiState,
    scheduleGenUiState: ScheduleGenUiState,
    registryResultUiState: RegistryResultUiState,
    onBackClick: () -> Unit = {},
    onAccountChange: (account: Account) -> Unit = {},
    onTargetCalendarChange: (calendar: Calendar) -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onAddClick: (ScheduleEvent) -> Unit = {},
    onEventChange: (ScheduleEvent) -> Unit = {},
    onDeleteClick: (ScheduleEvent) -> Unit = {},
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
                    duration = SnackbarDuration.Long,
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
        var editTargetEvent: ScheduleEvent? by remember { mutableStateOf(null) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "戻る",
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onRefreshClick) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "更新",
                            )
                        }
                    },
                )
            },
            bottomBar = {
                ScheduleGenBottomAppBar(
                    drawerState = drawerState,
                    generatedEventsUiState = generatedEventsUiState,
                    registryResultUiState = registryResultUiState,
                    calendarAccountUiState = calendarAccountUiState,
                    onAddClick = { event ->
                        onAddClick(event)
                        editTargetEvent = event
                    },
                    onRegistryClick = onRegistryClick,
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { paddingValues ->
            CarouselScheduleGenContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                scheduleInput = scheduleGenUiState.prompt,
                generatedEventsUiState = generatedEventsUiState,
                onEditClick = {
                    editTargetEvent = it
                },
                onRegistryClick = { event ->
                    if (calendarAccountUiState is CalendarAccountUiState.AccountSelected) {
                        onRegistryClick(
                            calendarAccountUiState.targetCalendar.id,
                            listOf(event),
                        )
                    }
                },
                onDeleteClick = onDeleteClick,
            )

            val sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true,
            )
            editTargetEvent?.let { event ->
                ModalBottomSheet(
                    onDismissRequest = {
                        onEventChange(event)
                        editTargetEvent = null
                    },
                    sheetState = sheetState,
                ) {
                    EditableEventContent(
                        event = event,
                        onEventChange = { newEvent ->
                            editTargetEvent = newEvent
                            onEventChange(newEvent)
                        },
                    )
                }
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
fun ScheduleGenScreenPreview_Generated_Carousel() {
    T2STheme {
        ScheduleGenScreen(
            calendarAccountUiState = CalendarAccountUiState.Initial,
            scheduleGenUiState = ScheduleGenUiState(
                prompt = "2020年2月15日1:30〜25日23:30　通常予定\n これはメモです\n ",
                generatedEventsUiState = GeneratedEventsUiState.Generated(fakeEvents),
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
