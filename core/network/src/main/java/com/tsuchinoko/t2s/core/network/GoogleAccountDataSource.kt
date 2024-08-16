package com.tsuchinoko.t2s.core.network

import android.accounts.Account
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import javax.inject.Inject

class GoogleAccountDataSource @Inject constructor(
    private val credential: GoogleAccountCredential,
) : AccountNetworkDataSource {
    override fun getAccount(): Account? = credential.selectedAccount

    override fun setAccountName(accountName: String) {
        credential.selectedAccountName = accountName
    }
}
