package com.tsuchinoko.t2s.feature.schedule.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.model.Calendar

@Composable
internal fun CalendarAccountGuideScreen(
    modifier: Modifier = Modifier,
    calendarAccountViewModel: CalendarAccountViewModel,
    onCompleteClick: () -> Unit = {},
) {
    val uiState by calendarAccountViewModel.calendarAccountUiState.collectAsState()
    CalendarAccountGuideScreen(
        modifier = modifier,
        uiState = uiState,
        onAccountChange = calendarAccountViewModel::updateAccount,
        onTargetCalendarChange = calendarAccountViewModel::updateTargetCalendar,
        onCompleteClick = onCompleteClick,
    )
}

@Composable
private fun CalendarAccountGuideScreen(
    modifier: Modifier = Modifier,
    uiState: CalendarAccountUiState,
    onAccountChange: (account: Account) -> Unit = {},
    onTargetCalendarChange: (calendar: Calendar) -> Unit = {},
    onCompleteClick: () -> Unit = {},
) {
    Scaffold(modifier = modifier) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))

            CalendarAccountSelection(
                uiState = uiState,
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                onAccountChange = onAccountChange,
                onCalendarChange = onTargetCalendarChange,
            )

            OutlinedButton(
                onClick = onCompleteClick,
                modifier = Modifier.padding(vertical = 24.dp),
            ) {
                Text(text = "設定完了")
            }
        }
    }
}

@Preview
@Composable
fun CalendarAccountGuideScreenPreview_Initial() {
    T2STheme {
        Surface {
            CalendarAccountGuideScreen(uiState = CalendarAccountUiState.Initial)
        }
    }
}

@Preview
@Composable
fun CalendarAccountGuideScreenPreview_AccountSelected() {
    T2STheme {
        Surface {
            CalendarAccountGuideScreen(uiState = fakeUiStateAccountSelected)
        }
    }
}
