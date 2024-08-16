package com.tsuchinoko.t2s.feature.schedule

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalInspectionMode
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.common.AccountPicker

/**
 * input: Selected Account Name
 */
class ChooseAccountContract : ActivityResultContract<String?, String?>() {
    override fun createIntent(context: Context, input: String?): Intent {
        val account = if (input != null) Account(input, "google") else null
        return AccountPicker.newChooseAccountIntent(
            AccountPicker
                .AccountChooserOptions
                .Builder()
                .setSelectedAccount(account)
                .build(),
        )
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return if (resultCode == RESULT_OK) {
            intent?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
        } else {
            return null
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GetAccountPermissionEffect() {
    if (LocalInspectionMode.current) return

    val accountPermissionState = rememberPermissionState(android.Manifest.permission.GET_ACCOUNTS)
    LaunchedEffect(accountPermissionState) {
        val status = accountPermissionState.status
        if (status is PermissionStatus.Denied && !status.shouldShowRationale) {
            accountPermissionState.launchPermissionRequest()
        }
    }
}
