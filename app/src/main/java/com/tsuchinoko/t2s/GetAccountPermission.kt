package com.tsuchinoko.t2s

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalInspectionMode
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

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
