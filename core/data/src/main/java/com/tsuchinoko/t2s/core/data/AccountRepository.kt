package com.tsuchinoko.t2s.core.data

import android.accounts.Account

interface AccountRepository {
    fun getAccount(): Account?
    fun setAccountName(accountName: String)
}