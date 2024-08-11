package com.tsuchinoko.t2s.core.data_google

import android.accounts.Account
import com.tsuchinoko.t2s.core.data.AccountRepository
import com.tsuchinoko.t2s.core.network.GoogleAccountDataSource
import javax.inject.Inject

class GoogleAccountRepository @Inject constructor(
    private val networkSource: GoogleAccountDataSource
) : AccountRepository {
    override fun getAccount(): Account? {
        return networkSource.getAccount()
    }

    override fun setAccountName(accountName: String) {
        networkSource.setAccountName(accountName)
    }
}
