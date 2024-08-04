package com.tsuchinoko.t2s

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tsuchinoko.t2s.ui.theme.T2STheme

@Composable
fun CalendarDrawerSheet(accountName: String?, calendarList: List<String>) {
    ModalDrawerSheet {
        if (accountName == null) {
            Button(onClick = {}) {
                Text("アカウントを選択")
            }
        } else {
            Text("選択中: $accountName")

            HorizontalDivider()

            SelectionCalendarList(calendarList = calendarList, modifier = Modifier.fillMaxWidth())
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
                accountName = null,
                calendarList = listOf()
            )
        }
    }
}

@Preview
@Composable
fun CalendarDrawerPreview_AccountSelected(){
    T2STheme {
        Surface {
            CalendarDrawerSheet(
                accountName = "taro@gmail.com",
                calendarList = listOf("calendar1", "calendar2", "calendar3")
            )
        }
    }
}
