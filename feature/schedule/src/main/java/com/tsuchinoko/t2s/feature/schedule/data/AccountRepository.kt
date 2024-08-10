package com.tsuchinoko.t2s.feature.schedule.data

import android.accounts.Account

interface AccountRepository {
    fun getAccount(): Account?
    fun setAccountName(accountName: String)
}