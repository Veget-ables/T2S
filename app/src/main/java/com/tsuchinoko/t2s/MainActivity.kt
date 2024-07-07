package com.tsuchinoko.t2s

import android.accounts.AccountManager
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
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.tsuchinoko.t2s.Constants.PREF_ACCOUNT_NAME
import com.tsuchinoko.t2s.Constants.REQUEST_ACCOUNT_PICKER
import com.tsuchinoko.t2s.Constants.REQUEST_AUTHORIZATION
import com.tsuchinoko.t2s.Constants.REQUEST_GOOGLE_PLAY_SERVICES
import com.tsuchinoko.t2s.Constants.REQUEST_PERMISSION_GET_ACCOUNTS
import com.tsuchinoko.t2s.ui.theme.T2STheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions

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
            REQUEST_GOOGLE_PLAY_SERVICES -> if (resultCode != RESULT_OK) {
                Log.d(
                    "T2S-LOG",
                    "This app requires Google Play Services. Please install " + "Google Play Services on your device and relaunch this app."
                )
            } else {
                getResultsFromApi()
            }

            REQUEST_ACCOUNT_PICKER -> if (resultCode == RESULT_OK && data != null &&
                data.extras != null
            ) {
                val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                if (accountName != null) {
                    val settings = getPreferences(Context.MODE_PRIVATE)
                    val editor = settings?.edit()
                    editor?.putString(PREF_ACCOUNT_NAME, accountName)
                    editor?.apply()
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
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices()
        } else if (mCredential!!.selectedAccountName == null) {
            chooseAccount()
        } else {
            makeRequestTask()
        }
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val connectionStatusCode = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(this)
        return connectionStatusCode == ConnectionResult.SUCCESS
    }

    private fun acquireGooglePlayServices() {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (connectionStatusCode != ConnectionResult.SUCCESS)
            if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
                Toast.makeText(
                    this,
                    "Google Play 開発者サービスが必要ですが、お使いの端末ではサポートされていません。",
                    LENGTH_SHORT
                ).show()
            }
    }

    private fun chooseAccount() {
        if (EasyPermissions.hasPermissions(this, android.Manifest.permission.GET_ACCOUNTS)) {
            val accountName = getPreferences(Context.MODE_PRIVATE)
                ?.getString(PREF_ACCOUNT_NAME, null)
            if (accountName != null) {
                mCredential!!.selectedAccountName = accountName
                getResultsFromApi()
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                    mCredential!!.newChooseAccountIntent(),
                    REQUEST_ACCOUNT_PICKER
                )
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
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
                getDataFromCalendar()
            } catch (e: Exception) {
                when (e) {
                    is GooglePlayServicesAvailabilityIOException -> {
                        Toast.makeText(
                            this@MainActivity,
                            "Google Error:" + e.connectionStatusCode,
                            LENGTH_SHORT
                        ).show()
                    }

                    is UserRecoverableAuthIOException -> {
                        this@MainActivity.startActivityForResult(
                            e.intent,
                            REQUEST_AUTHORIZATION
                        )
                    }

                    else -> {
                        Log.e("T2S-LOG", "The following error occurred:\n" + e.message)
                    }
                }
            }
        }
    }

    private fun getDataFromCalendar(): MutableList<GetEventModel> {
        val now = DateTime(System.currentTimeMillis())
        val eventStrings = ArrayList<GetEventModel>()
        val events = mService!!.events().list("primary")
            .setMaxResults(10)
            .setTimeMin(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute()
        val items = events.items

        for (event in items) {
            var start = event.start.dateTime
            if (start == null) {
                start = event.start.date
            }

            eventStrings.add(
                GetEventModel(
                    summary = event.summary,
                    startDate = start.toString()
                )
            )
        }
        return eventStrings
    }
}

object Constants {
    const val REQUEST_ACCOUNT_PICKER = 1000
    const val REQUEST_AUTHORIZATION = 1001
    const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    const val REQUEST_PERMISSION_GET_ACCOUNTS = 1003
    const val PREF_ACCOUNT_NAME = "getCalendarEvent"
}

data class GetEventModel(
    var id: Int = 0,
    var summary: String? = "",
    var startDate: String = "",
)