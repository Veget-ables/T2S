package com.tsuchinoko.t2s.feature.schedule.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.tsuchinoko.t2s.feature.schedule.R
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountSelection
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountUiState
import kotlinx.coroutines.launch

@Composable
internal fun ScheduleInputScreen(
    modifier: Modifier = Modifier,
    initialInput: String,
    scheduleInputViewModel: ScheduleInputViewModel = hiltViewModel(),
    onGenerateClick: (input: String) -> Unit = {},
) {
    val calendarAccountUiState by scheduleInputViewModel.calendarAccountUiState.collectAsState()

    ScheduleInputScreen(
        modifier = modifier,
        uiState = calendarAccountUiState,
        initialInput = initialInput,
        onGenerateClick = onGenerateClick,
    )
}

@Composable
private fun ScheduleInputScreen(
    modifier: Modifier = Modifier,
    uiState: CalendarAccountUiState,
    initialInput: String,
    onAccountChange: (accountName: String) -> Unit = {},
    onTargetCalendarChange: (calendar: Calendar) -> Unit = {},
    onGenerateClick: (input: String) -> Unit = {},
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                CalendarAccountSelection(
                    uiState = uiState,
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
                            onClick = {},
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.assignment),
                                contentDescription = "クリップボードから貼り付け",
                            )
                        }

                        IconButton(
                            onClick = {},
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.open),
                                contentDescription = "ファイルからインポート",
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        IconButton(
                            onClick = {},
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.delete),
                                contentDescription = "削除",
                            )
                        }
                    },
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var input by rememberSaveable { mutableStateOf(initialInput) }
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier
                        .padding(start = 36.dp, top = 36.dp, end = 36.dp)
                        .weight(1f)
                        .fillMaxWidth(),
                )

                OutlinedButton(
                    onClick = { onGenerateClick(input) },
                    modifier = Modifier.padding(vertical = 36.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.convert_to_event),
                        contentDescription = null,
                    )

                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))

                    Text(text = "予定オブジェクトに変換")
                }
            }
        }
    }
}

@Preview
@Composable
fun ScheduleInputScreenPreview() {
    T2STheme {
        ScheduleInputScreen(
            uiState = CalendarAccountUiState.Initial,
            initialInput = "",
        )
    }
}