package com.tsuchinoko.t2s.feature.schedule.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.feature.schedule.R

@Composable
internal fun CalendarAccountGuideScreen(
    modifier: Modifier = Modifier,
    onCompleteClick: () -> Unit = {},
) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.perm_contact_calendar),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 48.dp)
                    .size(80.dp),
            )

            Text(
                text = "登録先のアカウントとカレンダーを選択してください",
                modifier = Modifier.padding(32.dp),
                style = MaterialTheme.typography.headlineMedium,
            )

            CalendarAccountSelection(
                uiState = CalendarUiState.Initial,
                onAccountChange = {},
                onCalendarChange = {},
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
fun CalendarAccountGuideScreenPreview() {
    T2STheme {
        Surface {
            CalendarAccountGuideScreen()
        }
    }
}
