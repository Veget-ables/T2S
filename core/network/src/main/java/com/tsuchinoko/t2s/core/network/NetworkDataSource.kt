package com.tsuchinoko.t2s.core.network

import android.accounts.Account
import com.tsuchinoko.t2s.core.model.Calendar

interface NetworkDataSource {
    fun getAccount(): Account?
    fun setAccountName(accountName: String)
    suspend fun getCalendars(): List<Calendar>
}
