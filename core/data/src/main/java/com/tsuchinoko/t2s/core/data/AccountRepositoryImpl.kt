package com.tsuchinoko.t2s.core.data

import android.accounts.Account
import com.tsuchinoko.t2s.core.network.GoogleAccountDataSource
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val networkSource: GoogleAccountDataSource
) : AccountRepository {
    override fun getAccount(): Account? {
        return networkSource.getAccount()
    }

    override fun setAccountName(accountName: String) {
        networkSource.setAccountName(accountName)
    }
}
