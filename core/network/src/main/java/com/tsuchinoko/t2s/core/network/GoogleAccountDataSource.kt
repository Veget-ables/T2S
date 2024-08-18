package com.tsuchinoko.t2s.core.network

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.tsuchinoko.t2s.core.model.Account
import javax.inject.Inject

class GoogleAccountDataSource @Inject constructor(
    private val credential: GoogleAccountCredential,
) : AccountNetworkDataSource {
    override fun setAccount(account: Account) {
        credential.selectedAccountName = account.id.value
    }
}
