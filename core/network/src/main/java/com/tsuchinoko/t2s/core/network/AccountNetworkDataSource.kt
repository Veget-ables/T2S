package com.tsuchinoko.t2s.core.network

import com.tsuchinoko.t2s.core.model.Account

interface AccountNetworkDataSource {
    fun setAccount(account: Account)
}
