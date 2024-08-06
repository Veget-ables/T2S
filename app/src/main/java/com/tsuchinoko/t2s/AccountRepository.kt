package com.tsuchinoko.t2s

import android.accounts.Account

interface AccountRepository {
    fun getAccount(): Account?
    fun setAccountName(accountName: String)
}