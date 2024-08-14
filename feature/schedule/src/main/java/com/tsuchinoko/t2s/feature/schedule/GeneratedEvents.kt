package com.tsuchinoko.t2s.feature.schedule

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.core.model.ScheduleEvent

internal sealed interface GeneratedEventsUiState {
    data object Empty : GeneratedEventsUiState
    data object Loading : GeneratedEventsUiState
    data class Generated(val events: List<ScheduleEvent>) : GeneratedEventsUiState
    data class Error(val message: String) : GeneratedEventsUiState
}

@Composable
internal fun GeneratedEvents(
    uiState: GeneratedEventsUiState,
    modifier: Modifier = Modifier,
    onEventChange: (ScheduleEvent) -> Unit = {}
) {
    when (uiState) {
        GeneratedEventsUiState.Empty -> {
            Text("予定を変換してください")
        }

        GeneratedEventsUiState.Loading -> {
            CircularProgressIndicator()
        }

        is GeneratedEventsUiState.Generated -> {
            LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 8.dp,
                    top = 16.dp,
                    bottom = 112.dp
                )
            ) {
                items(
                    items = uiState.events,
                    key = { it.id }
                ) { event ->
                    GeneratedEvent(
                        event = event,
                        modifier = Modifier.fillMaxWidth(),
                        onEventChange = onEventChange
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        is GeneratedEventsUiState.Error -> {
            Text(
                text = uiState.message,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
fun GeneratedEventsPreview_Empty() {
    T2STheme {
        Surface {
            GeneratedEvents(uiState = GeneratedEventsUiState.Empty)
        }
    }
}

@Preview
@Composable
fun GeneratedEventsPreview_Generated() {
    T2STheme {
        Surface {
            GeneratedEvents(uiState = GeneratedEventsUiState.Generated(fakeEvents))
        }
    }
}

@Preview
@Composable
fun GeneratedEventsPreview_Error() {
    T2STheme {
        Surface {
            GeneratedEvents(uiState = GeneratedEventsUiState.Error("予定の生成に失敗しました"))
        }
    }
}
