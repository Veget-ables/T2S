package com.tsuchinoko.t2s

import android.accounts.Account
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tsuchinoko.t2s.ui.theme.T2STheme

@Composable
fun CalendarDrawerSheet(
    uiState: CalendarUiState,
    modifier : Modifier = Modifier,
    onAccountChange: (accountName: String) -> Unit = {}
) {
    ModalDrawerSheet(modifier = modifier) {
        val launcher =
            rememberLauncherForActivityResult(ChooseAccountContract()) { newAccountName ->
                newAccountName?.let {
                    onAccountChange(it)
                }
            }

        when(uiState) {
            CalendarUiState.Initial -> {
                TextButton(
                    onClick = {
                        launcher.launch(null)
                    }
                ) {
                    Text("アカウントを選択")
                }

                HorizontalDivider()

                Spacer(Modifier.height(20.dp))

                Text(
                    "カレンダーを表示するためにはアカウントの選択が必要です",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            CalendarUiState.Loading -> {

            }

            is CalendarUiState.Success -> {
                val account = uiState.account
                TextButton(
                    onClick = {
                        launcher.launch(account)
                    }
                ) {
                    val accountText = "選択中: ${account.name}"
                    Text(accountText)
                }

                HorizontalDivider()

                SelectionCalendarList(
                    calendarList = uiState.calendarList,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            is CalendarUiState.Error -> {

            }
        }
    }
}

@Composable
private fun SelectionCalendarList(calendarList: List<String>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(calendarList) { calendar ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = false,
                    onClick = {}
                )
                Text(calendar)
            }
        }
    }
}

@Preview
@Composable
fun CalendarDrawerPreview_NoAccount() {
    T2STheme {
        Surface {
            CalendarDrawerSheet(
                uiState = CalendarUiState.Initial
            )
        }
    }
}

@Preview
@Composable
fun CalendarDrawerPreview_AccountSelected() {
    T2STheme {
        Surface {
            CalendarDrawerSheet(
                uiState = CalendarUiState.Success(
                    account = Account("taro", "google"),
                    calendarList = listOf("calendar1", "calendar2", "calendar3")
                )
            )
        }
    }
}
