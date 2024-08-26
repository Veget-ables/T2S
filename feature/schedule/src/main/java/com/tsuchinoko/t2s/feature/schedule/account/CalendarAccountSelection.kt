package com.tsuchinoko.t2s.feature.schedule.account

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.model.AccountId
import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.CalendarId

internal sealed interface CalendarAccountUiState {
    data object Initial : CalendarAccountUiState
    data object Loading : CalendarAccountUiState

    data class AccountSelected(
        val account: Account,
        val calendars: List<Calendar>,
        val targetCalendar: Calendar,
    ) : CalendarAccountUiState

    data class Error(val message: String) : CalendarAccountUiState
    data class RecoverableIntentError(val intent: Intent) : CalendarAccountUiState
}

@Composable
internal fun CalendarAccountSelection(
    uiState: CalendarAccountUiState,
    modifier: Modifier = Modifier,
    onAccountChange: (account: Account) -> Unit = {},
    onCalendarChange: (calendar: Calendar) -> Unit = {},
) {
    var selectedAccount: Account? by remember {
        mutableStateOf(null)
    }

    val launcher =
        rememberLauncherForActivityResult(ChooseAccountContract()) { account ->
            account?.let {
                onAccountChange(it)
                selectedAccount = it
            }
        }

    val recoverableLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.resultCode
            selectedAccount?.let {
                onAccountChange(it)
            }
        }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {
            CalendarAccountUiState.Initial -> {
                TextButton(
                    onClick = {
                        launcher.launch(null)
                    },
                ) {
                    Text("アカウントを選択")
                }
            }

            CalendarAccountUiState.Loading -> {
                CircularProgressIndicator()
            }

            is CalendarAccountUiState.AccountSelected -> {
                val account = uiState.account
                TextButton(
                    onClick = {
                        launcher.launch(account)
                    },
                ) {
                    Text("選択中: ${account.id.value}")
                }

                HorizontalDivider()

                Text(
                    text = "対象のカレンダーを一つ選択してください",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp),
                )

                AccountCalendars(
                    selectedCalendar = uiState.targetCalendar,
                    calendars = uiState.calendars,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    onCalendarChange = onCalendarChange,
                )
            }

            is CalendarAccountUiState.Error -> {
                Text(uiState.message)
            }

            is CalendarAccountUiState.RecoverableIntentError -> {
                LaunchedEffect(uiState) {
                    recoverableLauncher.launch(uiState.intent)
                }
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCalendarChange(calendar) }
                    .padding(16.dp),
            ) {
                RadioButton(
                    selected = calendar == selectedCalendar,
                    onClick = null,
                )
                Text(
                    text = calendar.title,
                    modifier = Modifier.padding(start = 16.dp),
                )
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
            CalendarAccountSelection(uiState = fakeUiStateAccountSelected)
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

private val calendar1 = Calendar(id = CalendarId("1"), title = "calendar1")
private val calendar2 = Calendar(id = CalendarId("2"), title = "calendar2")
private val calendar3 = Calendar(id = CalendarId("3"), title = "calendar3")

internal val fakeUiStateAccountSelected = CalendarAccountUiState.AccountSelected(
    account = Account(AccountId("taro")),
    calendars = listOf(calendar1, calendar2, calendar3),
    targetCalendar = calendar1,
)
