package com.tsuchinoko.t2s.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tsuchinoko.t2s.core.datastore.di.CalendarDataStore
import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.CalendarId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CalendarPreferencesDataStore @Inject constructor(
    @CalendarDataStore private val preferences: DataStore<Preferences>,
) {
    fun getTargetCalendarId(): Flow<CalendarId?> = preferences.data.map { prefs ->
        val id = prefs[stringPreferencesKey(TARGET_CALENDAR_ID)]
        if (id != null) CalendarId(id) else null
    }

    suspend fun setTargetCalendar(calendar: Calendar) {
        preferences.edit { prefs ->
            prefs[stringPreferencesKey(TARGET_CALENDAR_ID)] = calendar.id.value
        }
    }

    companion object {
        const val TARGET_CALENDAR_ID = "target_calendar_id"
    }
}
