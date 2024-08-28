package com.tsuchinoko.t2s.core.google

import com.tsuchinoko.core.database.dao.AccountDao
import com.tsuchinoko.core.database.entity.convertToEntity
import com.tsuchinoko.core.database.entity.convertToModel
import com.tsuchinoko.t2s.core.data.AccountRepository
import com.tsuchinoko.t2s.core.datastore.AccountPreferencesDataStore
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.network.GoogleAccountDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoogleAccountRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val dataStore: AccountPreferencesDataStore,
    private val networkSource: GoogleAccountDataSource,
) : AccountRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAccount(): Flow<Account?> = dataStore.getTargetAccountId()
        .flatMapLatest { id ->
            if (id == null) {
                flow { emit(null) }
            } else {
                accountDao
                    .get(id.value)
                    .map { entity ->
                        entity ?: return@map null
                        entity.convertToModel().also { account ->
                            networkSource.setAccount(account)
                        }
                    }
            }
        }

    override suspend fun setAccount(account: Account) {
        accountDao.insert(account.convertToEntity())
        dataStore.setTargetAccount(account)
    }
}
