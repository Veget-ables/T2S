package com.tsuchinoko.t2s.feature.schedule.account

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.CalendarId
import com.tsuchinoko.t2s.feature.schedule.ChooseAccountContract

internal sealed interface CalendarAccountUiState {
    data object Initial : CalendarAccountUiState
    data object Loading : CalendarAccountUiState

    data class AccountSelected(
        val accountName: String,
        val calendars: List<Calendar>,
        val targetCalendar: Calendar,
    ) : CalendarAccountUiState

    data class Error(val message: String) : CalendarAccountUiState
}

@Composable
internal fun CalendarAccountSelection(
    uiState: CalendarAccountUiState,
    modifier: Modifier = Modifier,
    onAccountChange: (accountName: String) -> Unit = {},
    onCalendarChange: (calendar: Calendar) -> Unit = {},
) {
    val launcher =
        rememberLauncherForActivityResult(ChooseAccountContract()) { newAccountName ->
            newAccountName?.let {
                onAccountChange(it)
            }
        }

    Column(modifier = modifier) {
        when (uiState) {
            CalendarAccountUiState.Initial -> {
                TextButton(
                    onClick = {
                        launcher.launch(null)
                    },
                ) {
                    Text("アカウントを選択")
                }

                HorizontalDivider()

                Spacer(Modifier.height(20.dp))

                Text(
                    "カレンダーを表示するためにはアカウントの選択が必要です",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            CalendarAccountUiState.Loading -> {
                CircularProgressIndicator()
            }

            is CalendarAccountUiState.AccountSelected -> {
                val accountName = uiState.accountName
                TextButton(
                    onClick = {
                        launcher.launch(accountName)
                    },
                ) {
                    Text("選択中: $accountName")
                }

                HorizontalDivider()

                AccountCalendars(
                    selectedCalendar = uiState.targetCalendar,
                    calendars = uiState.calendars,
                    modifier = Modifier.fillMaxWidth(),
                    onCalendarChange = onCalendarChange,
                )
            }

            is CalendarAccountUiState.Error -> {
                Text(uiState.message)
            }
        }
    }
}

@Composable
private fun AccountCalendars(
    selectedCalendar: Calendar,
    calendars: List<Calendar>,
    modifier: Modifier = Modifier,
    onCalendarChange: (calendar: Calendar) -> Unit = {},
) {
    LazyColumn(modifier = modifier) {
        items(calendars) { calendar ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = calendar == selectedCalendar,
                    onClick = { onCalendarChange(calendar) },
                )
                Text(calendar.title)
            }
        }
    }
}

@Preview
@Composable
fun CalendarAccountSelectionPreview_Initial() {
    T2STheme {
        Surface {
            CalendarAccountSelection(
                uiState = CalendarAccountUiState.Initial,
            )
        }
    }
}

@Preview
@Composable
fun CalendarAccountSelectionPreview_AccountSelected() {
    T2STheme {
        Surface {
            val calendar1 = Calendar(id = CalendarId("1"), title = "calendar1")
            val calendar2 = Calendar(id = CalendarId("2"), title = "calendar2")
            val calendar3 = Calendar(id = CalendarId("3"), title = "calendar3")
            CalendarAccountSelection(
                uiState = CalendarAccountUiState.AccountSelected(
                    accountName = "taro",
                    calendars = listOf(calendar1, calendar2, calendar3),
                    targetCalendar = calendar1,
                ),
            )
        }
    }
}

@Preview
@Composable
fun CalendarAccountSelectionPreview_Error() {
    T2STheme {
        Surface {
            CalendarAccountSelection(
                uiState = CalendarAccountUiState.Error("アカウントの取得に失敗しました"),
            )
        }
    }
}