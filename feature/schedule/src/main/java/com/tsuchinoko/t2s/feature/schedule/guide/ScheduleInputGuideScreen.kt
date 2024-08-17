package com.tsuchinoko.t2s.feature.schedule.guide

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.feature.schedule.R

@Composable
internal fun ScheduleInputGuideScreen(
    modifier: Modifier = Modifier,
    onInputClick: (prompt: String) -> Unit = {},
) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(R.drawable.edit_text),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 48.dp)
                    .size(80.dp),
            )

            Text(
                text = "予定に変換するテキストを入力してください",
                modifier = Modifier.padding(32.dp),
                style = MaterialTheme.typography.headlineMedium,
            )

            OutlinedButton(onClick = { onInputClick("") }) {
                Icon(
                    painter = painterResource(R.drawable.edit),
                    contentDescription = null,
                )

                Spacer(Modifier.width(ButtonDefaults.IconSpacing))

                Text(text = "直接手入力")
            }

            Text(
                "or",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.labelSmall,
            )

            val clipboardManager = LocalClipboardManager.current
            OutlinedButton(
                onClick = {
                    val clipboardText = clipboardManager.getText().toString()
                    onInputClick(clipboardText)
                },
            ) {
                Icon(
                    painter = painterResource(R.drawable.assignment),
                    contentDescription = null,
                )

                Spacer(Modifier.width(ButtonDefaults.IconSpacing))

                Text(text = "クリップボードから貼り付け")
            }

            Text(
                "or",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.labelSmall,
            )

            OutlinedButton(onClick = {}) {
                Icon(
                    painter = painterResource(R.drawable.open),
                    contentDescription = null,
                )

                Spacer(Modifier.width(ButtonDefaults.IconSpacing))

                Text(text = "ファイルからインポート")
            }
        }
    }
}

@Preview
@Composable
fun ScheduleInputGuideScreenPreview() {
    T2STheme {
        Surface {
            ScheduleInputGuideScreen()
        }
    }
}
