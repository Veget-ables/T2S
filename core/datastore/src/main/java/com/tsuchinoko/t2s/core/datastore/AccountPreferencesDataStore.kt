package com.tsuchinoko.t2s.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tsuchinoko.t2s.core.datastore.di.AccountDataStore
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.model.AccountId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountPreferencesDataStore @Inject constructor(
    @AccountDataStore private val preferences: DataStore<Preferences>,
) {
    fun getTargetAccountId(): Flow<AccountId?> = preferences.data.map { prefs ->
        val id = prefs[stringPreferencesKey(TARGET_ACCOUNT_ID)]
        if (id != null) AccountId(id) else null
    }

    suspend fun setTargetAccount(account: Account) {
        preferences.edit { prefs ->
            prefs[stringPreferencesKey(TARGET_ACCOUNT_ID)] = account.id.value
        }
    }

    companion object {
        const val TARGET_ACCOUNT_ID = "target_account_id"
    }
}
