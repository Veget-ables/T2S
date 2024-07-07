package com.tsuchinoko.t2s

import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.tsuchinoko.t2s.Constants.REQUEST_ACCOUNT_PICKER
import com.tsuchinoko.t2s.Constants.REQUEST_AUTHORIZATION
import com.tsuchinoko.t2s.Constants.REQUEST_PERMISSION_GET_ACCOUNTS
import com.tsuchinoko.t2s.ui.theme.T2STheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initGCPClient(this)

        setContent {
            T2STheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    ScheduleGenScreen(
                        onRegistryClick = {
                            getResultsFromApi()
                        }
                    )
                }
            }
        }
    }

    private var mCredential: GoogleAccountCredential? = null
    private var mService: Calendar? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_ACCOUNT_PICKER -> if (resultCode == RESULT_OK) {
                val accountName = data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    mCredential!!.selectedAccountName = accountName
                    getResultsFromApi()
                }
            }

            REQUEST_AUTHORIZATION -> if (resultCode == RESULT_OK) {
                getResultsFromApi()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun initGCPClient(context: Context) {
        mCredential = GoogleAccountCredential
            .usingOAuth2(
                context,
                arrayListOf(CalendarScopes.CALENDAR)
            )
            .setBackOff(ExponentialBackOff())

        mService = Calendar.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            mCredential
        )
            .setApplicationName("T2S")
            .build()
    }

    private fun getResultsFromApi() {
        if (isGooglePlayServicesAvailable()) {
            if (mCredential!!.selectedAccountName == null) {
                chooseAccount()
            } else {
                makeRequestTask()
            }
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

    private fun chooseAccount() {
        if (EasyPermissions.hasPermissions(this, android.Manifest.permission.GET_ACCOUNTS)) {
            startActivityForResult(
                mCredential!!.newChooseAccountIntent(),
                REQUEST_ACCOUNT_PICKER
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "This app needs to access your Google account (via Contacts).",
                REQUEST_PERMISSION_GET_ACCOUNTS,
                android.Manifest.permission.GET_ACCOUNTS
            )
        }
    }

    private fun makeRequestTask() {
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
    const val REQUEST_ACCOUNT_PICKER = 1000
    const val REQUEST_AUTHORIZATION = 1001
    const val REQUEST_PERMISSION_GET_ACCOUNTS = 1002
}
