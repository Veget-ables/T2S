package com.tsuchinoko.core.database.entity

import androidx.room.Entity
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.CalendarId

@Entity(primaryKeys = ["id", "accountId"])
data class CalendarEntity(
    val id: String,
    val accountId: String,
    val title: String,
)

fun CalendarEntity.convertToModel(): Calendar = Calendar(
    id = CalendarId(id),
    title = title,
)

fun Calendar.convertToEntity(account: Account): CalendarEntity = CalendarEntity(
    id = id.value,
    accountId = account.id.value,
    title = title,
)
