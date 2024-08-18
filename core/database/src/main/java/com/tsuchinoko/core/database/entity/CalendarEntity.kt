package com.tsuchinoko.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CalendarEntity(
    @PrimaryKey val id: String,
    val title: String,
)
