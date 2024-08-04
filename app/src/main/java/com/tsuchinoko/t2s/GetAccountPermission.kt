package com.tsuchinoko.t2s

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

class ChooseAccountContract : ActivityResultContract<Account?, String?>() {
    override fun createIntent(context: Context, input: Account?): Intent {
        return AccountPicker.newChooseAccountIntent(
            AccountPicker
                .AccountChooserOptions
                .Builder()
                .setSelectedAccount(input)
                .build()
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
internal fun GetAccountPermissionEffect() {
    if (LocalInspectionMode.current) return

    val accountPermissionState = rememberPermissionState(android.Manifest.permission.GET_ACCOUNTS)
    LaunchedEffect(accountPermissionState) {
        val status = accountPermissionState.status
        if (status is PermissionStatus.Denied && !status.shouldShowRationale) {
            accountPermissionState.launchPermissionRequest()
        }
    }
}
