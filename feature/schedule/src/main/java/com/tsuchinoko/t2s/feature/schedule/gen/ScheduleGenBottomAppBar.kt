package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.core.model.BaseInput
import com.tsuchinoko.t2s.core.model.CalendarId
import com.tsuchinoko.t2s.core.model.EventId
import com.tsuchinoko.t2s.core.model.ScheduleEvent
import com.tsuchinoko.t2s.feature.schedule.R
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountUiState
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.UUID

@Composable
internal fun ScheduleGenBottomAppBar(
    drawerState: DrawerState,
    generatedEventsUiState: GeneratedEventsUiState,
    registryResultUiState: RegistryResultUiState,
    calendarAccountUiState: CalendarAccountUiState,
    onAddClick: (ScheduleEvent) -> Unit = {},
    onRegistryClick: (calendarId: CalendarId, events: List<ScheduleEvent>) -> Unit = { _, _ -> },
) {
    val scope = rememberCoroutineScope()
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
            val clipboardManager: ClipboardManager = LocalClipboardManager.current
            IconButton(
                onClick = {
                    if (generatedEventsUiState is GeneratedEventsUiState.Generated) {
                        clipboardManager.setText(AnnotatedString(generatedEventsUiState.events.copiedText))
                    }
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.copy),
                    contentDescription = "コピー",
                )
            }

            IconButton(
                onClick = {
                    val now = LocalDateTime.now()
                    val newEvent = ScheduleEvent(
                        id = EventId(value = UUID.randomUUID()),
                        title = "",
                        memo = "",
                        start = now,
                        end = now,
                        base = BaseInput(title = "", date = ""),
                    )
                    onAddClick(newEvent)
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "追加",
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
}

@Preview
@Composable
fun ScheduleGenBottomAppBarPreview_Loading() {
    T2STheme {
        ScheduleGenBottomAppBar(
            drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
            calendarAccountUiState = CalendarAccountUiState.Initial,
            generatedEventsUiState = GeneratedEventsUiState.Loading,
            registryResultUiState = RegistryResultUiState.Standby,
        )
    }
}

@Preview
@Composable
fun ScheduleGenBottomAppBarPreview() {
    T2STheme {
        ScheduleGenBottomAppBar(
            drawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
            calendarAccountUiState = CalendarAccountUiState.Initial,
            generatedEventsUiState = GeneratedEventsUiState.Generated(emptyList()),
            registryResultUiState = RegistryResultUiState.Standby,
        )
    }
}
