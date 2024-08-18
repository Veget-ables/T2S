package com.tsuchinoko.t2s.core.data

import com.tsuchinoko.t2s.core.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAccount(): Flow<Account?>
    suspend fun setAccount(account: Account)
}
