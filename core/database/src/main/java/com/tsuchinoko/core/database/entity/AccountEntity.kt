package com.tsuchinoko.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AccountEntity(
    @PrimaryKey val userId: String,
)
