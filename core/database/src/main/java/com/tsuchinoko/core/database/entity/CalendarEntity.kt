package com.tsuchinoko.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.CalendarId

@Entity
data class CalendarEntity(
    @PrimaryKey val id: String,
    val title: String,
)

fun CalendarEntity.convertToModel(): Calendar = Calendar(id = CalendarId(id), title = title)

fun Calendar.convertToEntity(): CalendarEntity = CalendarEntity(id = id.value, title = title)
