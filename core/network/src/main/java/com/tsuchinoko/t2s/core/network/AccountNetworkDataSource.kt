package com.tsuchinoko.t2s.core.network

import android.accounts.Account

interface AccountNetworkDataSource {
    fun getAccount(): Account?
    fun setAccountName(accountName: String)
}
