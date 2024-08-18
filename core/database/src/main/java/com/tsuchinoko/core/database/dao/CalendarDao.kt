package com.tsuchinoko.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tsuchinoko.core.database.entity.CalendarEntity

@Dao
interface CalendarDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entities: List<CalendarEntity>)

    @Query("SELECT * FROM calendarentity")
    fun getAll(): List<CalendarEntity>
}
