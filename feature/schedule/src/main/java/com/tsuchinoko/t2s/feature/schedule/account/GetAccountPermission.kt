package com.tsuchinoko.t2s.feature.schedule.account

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
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.model.AccountId

/**
 * input: Selected Account Name
 */
class ChooseAccountContract : ActivityResultContract<Account?, Account?>() {
    override fun createIntent(context: Context, input: Account?): Intent {
        val account = input?.toSystemAccount("google")
        return AccountPicker.newChooseAccountIntent(
            AccountPicker
                .AccountChooserOptions
                .Builder()
                .setSelectedAccount(account)
                .build(),
        )
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Account? {
        return if (resultCode == RESULT_OK) {
            val accountName = intent?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME) ?: return null
            Account(AccountId(accountName))
        } else {
            return null
        }
    }
}

private fun Account.toSystemAccount(type: String): android.accounts.Account = android.accounts.Account(id.value, type)

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
