package com.tsuchinoko.t2s

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.feature.schedule.CalendarAccountGuide
import com.tsuchinoko.t2s.feature.schedule.GetAccountPermissionEffect
import com.tsuchinoko.t2s.feature.schedule.scheduleNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            T2STheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = CalendarAccountGuide,
                ) {
                    scheduleNavigation(navController)
                }
                GetAccountPermissionEffect()
            }
        }
    }
}

// TODO
//
//    private var mService: Calendar? = null
//
//    private fun chooseCalendar() {
//        if (isGooglePlayServicesAvailable()) {
//        } else {
//            Toast.makeText(
//                this,
//                "Google Play 開発者サービスが必要ですが、お使いの端末では無効になっているかサポートされていません。",
//                LENGTH_SHORT
//            ).show()
//        }
//    }
//
//    private fun isGooglePlayServicesAvailable(): Boolean {
//        val connectionStatusCode = GoogleApiAvailability.getInstance()
//            .isGooglePlayServicesAvailable(this)
//        return connectionStatusCode == ConnectionResult.SUCCESS
//    }
//
//    private fun insertEvents() {
//        lifecycleScope.launch(Dispatchers.IO) {
//            try {
//                listOf(
//                    createScheduleEvent(
//                        title = "終日予定",
//                        start = LocalDateTime.parse("2024-07-07T00:00"),
//                        end = LocalDateTime.parse("2024-07-07T23:59")
//                    ),
//                    createScheduleEvent(
//                        title = "通常予定",
//                        start = LocalDateTime.parse("2024-07-07T02:00"),
//                        end = LocalDateTime.parse("2024-07-07T08:30")
//                    )
//                ).forEach {
//                    insertEvent(it)
//                }
//            } catch (e: Exception) {
//                when (e) {
//                    is UserRecoverableAuthIOException -> {
//                        this@MainActivity.startActivityForResult(
//                            e.intent,
//                            REQUEST_AUTHORIZATION
//                        )
//                    }
//
//                    else -> {
//                        Log.e("GOOGLE_ERROR", "Google Calendarとの連携に失敗しました：" + e.message)
//                    }
//                }
//            }
//        }
//    }
//
//    private fun insertEvent(scheduleEvent: ScheduleEvent) {
//        val (startDateTime, endDateTime) = if (scheduleEvent.isAllDay) {
//            val startDateTime = EventDateTime()
//                .setDate(
//                    DateTime(scheduleEvent.start.toLocalDate().toString())
//                )
//            startDateTime to startDateTime
//        } else {
//            val startDateTime = scheduleEvent.start.toEventDateTime()
//            val endDateTime = scheduleEvent.end.toEventDateTime()
//            startDateTime to endDateTime
//        }
//
//        val event = Event()
//            .setStart(startDateTime)
//            .setEnd(endDateTime)
//            .setSummary(scheduleEvent.title)
//
//        val resultEvent = mService!!
//            .events()
//            .insert("primary", event)
//            .execute()
//        System.out.printf("Event created: %s\n", resultEvent.htmlLink)
//    }
// }
//
// @SuppressLint("SimpleDateFormat")
// private fun LocalDateTime.toEventDateTime(): EventDateTime {
//    val zonedDateTime = atZone(ZoneId.systemDefault())
//    val date = Date.from(zonedDateTime.toInstant())
//    return EventDateTime()
//        .setDateTime(
//            DateTime(
//                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(date)
//            )
//        )
// }
//
// object Constants {
//    const val REQUEST_AUTHORIZATION = 1001
// }
