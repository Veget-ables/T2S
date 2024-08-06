package com.tsuchinoko.t2s

import android.accounts.Account
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val credential: GoogleAccountCredential
) : AccountRepository {
    override fun getAccount(): Account? {
        return credential.selectedAccount
    }

    override fun setAccountName(accountName: String) {
        credential.selectedAccountName = accountName
    }
}
