package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.feature.schedule.R

@Composable
internal fun ScheduleInputCard(
    text: String,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit = {},
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Card(
        modifier = modifier
            .animateContentSize(),
    ) {
        OutlinedButton(
            onClick = onEditClick,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                .align(Alignment.End),
        ) {
            Icon(
                painter = painterResource(R.drawable.edit_text),
                contentDescription = null,
            )

            Spacer(Modifier.width(ButtonDefaults.IconSpacing))

            Text(text = "編集")
        }

        if (isExpanded) {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )

            TextButton(
                onClick = { isExpanded = false },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text("とじる")
            }
        } else {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                maxLines = 2,
            )

            TextButton(
                onClick = { isExpanded = true },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text("もっと")
            }
        }
    }
}

@Preview
@Composable
fun ScheduleInputCardPreview() {
    T2STheme {
        Column {
            ScheduleInputCard(text = "2020年2月15日1:30〜25日23:30　通常予定\n これはメモです\n ")
        }
    }
}
