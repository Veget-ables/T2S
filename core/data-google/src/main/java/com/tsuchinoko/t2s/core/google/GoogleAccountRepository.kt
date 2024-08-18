package com.tsuchinoko.t2s.core.google

import com.tsuchinoko.core.database.dao.AccountDao
import com.tsuchinoko.core.database.entity.convertToEntity
import com.tsuchinoko.core.database.entity.convertToModel
import com.tsuchinoko.t2s.core.data.AccountRepository
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.network.GoogleAccountDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoogleAccountRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val networkSource: GoogleAccountDataSource,
) : AccountRepository {
    override fun getAccount(): Flow<Account?> = accountDao.get().map { it?.convertToModel() }

    override suspend fun setAccount(account: Account) {
        accountDao.insert(account.convertToEntity())
        networkSource.setAccount(account)
    }
}
