package com.tsuchinoko.t2s.core.data

import android.accounts.Account
import com.tsuchinoko.t2s.core.network.GoogleServiceDataSource
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val service: GoogleServiceDataSource
) : AccountRepository {
    override fun getAccount(): Account? {
        return service.getAccount()
    }

    override fun setAccountName(accountName: String) {
        service.setAccountName(accountName)
    }
}
