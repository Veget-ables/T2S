package com.tsuchinoko.t2s.core.google

import com.tsuchinoko.core.database.dao.AccountDao
import com.tsuchinoko.core.database.entity.convertToEntity
import com.tsuchinoko.core.database.entity.convertToModel
import com.tsuchinoko.t2s.core.data.AccountRepository
import com.tsuchinoko.t2s.core.datastore.T2SPreferencesDataStore
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.network.GoogleAccountDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoogleAccountRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val dataStore: T2SPreferencesDataStore,
    private val networkSource: GoogleAccountDataSource,
) : AccountRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAccount(): Flow<Account?> = dataStore.getTargetAccountId()
        .flatMapMerge { id ->
            if (id == null) {
                flow { emit(null) }
            } else {
                accountDao
                    .get(id.value)
                    .map { it?.convertToModel() }
            }
        }

    override suspend fun setAccount(account: Account) {
        accountDao.insert(account.convertToEntity())
        dataStore.setTargetAccount(account)
        networkSource.setAccount(account)
    }
}
