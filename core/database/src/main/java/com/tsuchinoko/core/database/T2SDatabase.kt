package com.tsuchinoko.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tsuchinoko.core.database.dao.AccountDao
import com.tsuchinoko.core.database.dao.CalendarDao
import com.tsuchinoko.core.database.entity.AccountEntity
import com.tsuchinoko.core.database.entity.CalendarEntity

@Database(
    entities = [
        AccountEntity::class,
        CalendarEntity::class,
    ],
    version = 1,
)
internal abstract class T2SDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun calendarDao(): CalendarDao
}
