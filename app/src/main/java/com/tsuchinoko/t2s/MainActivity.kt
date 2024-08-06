package com.tsuchinoko.t2s

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.tsuchinoko.t2s.Constants.REQUEST_AUTHORIZATION
import com.tsuchinoko.t2s.ui.theme.T2STheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            T2STheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "schedule_gen"
                ) {
                    composable("schedule_gen") {
                        ScheduleGenScreen()
                    }
                }
                GetAccountPermissionEffect()
            }
        }
    }

    private var mService: Calendar? = null

    private fun chooseCalendar() {
        if (isGooglePlayServicesAvailable()) {
        } else {
            Toast.makeText(
                this,
                "Google Play 開発者サービスが必要ですが、お使いの端末では無効になっているかサポートされていません。",
                LENGTH_SHORT
            ).show()
        }
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val connectionStatusCode = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(this)
        return connectionStatusCode == ConnectionResult.SUCCESS
    }

    private fun insertEvents() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                listOf(
                    createScheduleEvent(
                        title = "終日予定",
                        start = LocalDateTime.parse("2024-07-07T00:00"),
                        end = LocalDateTime.parse("2024-07-07T23:59")
                    ),
                    createScheduleEvent(
                        title = "通常予定",
                        start = LocalDateTime.parse("2024-07-07T02:00"),
                        end = LocalDateTime.parse("2024-07-07T08:30")
                    )
                ).forEach {
                    insertEvent(it)
                }
            } catch (e: Exception) {
                when (e) {
                    is UserRecoverableAuthIOException -> {
                        this@MainActivity.startActivityForResult(
                            e.intent,
                            REQUEST_AUTHORIZATION
                        )
                    }

                    else -> {
                        Log.e("GOOGLE_ERROR", "Google Calendarとの連携に失敗しました：" + e.message)
                    }
                }
            }
        }
    }

    private fun insertEvent(scheduleEvent: ScheduleEvent) {
        val (startDateTime, endDateTime) = if (scheduleEvent.isAllDay) {
            val startDateTime = EventDateTime()
                .setDate(
                    DateTime(scheduleEvent.start.toLocalDate().toString())
                )
            startDateTime to startDateTime
        } else {
            val startDateTime = scheduleEvent.start.toEventDateTime()
            val endDateTime = scheduleEvent.end.toEventDateTime()
            startDateTime to endDateTime
        }

        val event = Event()
            .setStart(startDateTime)
            .setEnd(endDateTime)
            .setSummary(scheduleEvent.title)

        val resultEvent = mService!!
            .events()
            .insert("primary", event)
            .execute()
        System.out.printf("Event created: %s\n", resultEvent.htmlLink)
    }
}

@SuppressLint("SimpleDateFormat")
private fun LocalDateTime.toEventDateTime(): EventDateTime {
    val zonedDateTime = atZone(ZoneId.systemDefault())
    val date = Date.from(zonedDateTime.toInstant())
    return EventDateTime()
        .setDateTime(
            DateTime(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(date)
            )
        )
}

object Constants {
    const val REQUEST_AUTHORIZATION = 1001
}
