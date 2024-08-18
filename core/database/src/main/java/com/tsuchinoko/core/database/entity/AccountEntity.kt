package com.tsuchinoko.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.model.AccountId

@Entity
data class AccountEntity(
    @PrimaryKey val id: String,
)

fun AccountEntity.convertToModel(): Account = Account(AccountId(id))

fun Account.convertToEntity(): AccountEntity = AccountEntity(id.value)
