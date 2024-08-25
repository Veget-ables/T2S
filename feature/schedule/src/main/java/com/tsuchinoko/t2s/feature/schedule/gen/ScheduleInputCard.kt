package com.tsuchinoko.t2s.feature.schedule.gen

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 16.dp,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(
                onClick = { isExpanded = !isExpanded },
            ) {
                Icon(
                    painter = painterResource(if (isExpanded) R.drawable.collapse else R.drawable.expand),
                    contentDescription = null,
                )

                Spacer(Modifier.width(ButtonDefaults.IconSpacing))

                Text(if (isExpanded) "縮小" else "展開")
            }

            OutlinedButton(
                onClick = onEditClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.edit_text),
                    contentDescription = null,
                )

                Spacer(Modifier.width(ButtonDefaults.IconSpacing))

                Text(text = "編集")
            }
        }

        if (isExpanded) {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        } else {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                maxLines = 3,
            )
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
