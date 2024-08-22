package com.tsuchinoko.t2s.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tsuchinoko.t2s.core.model.Calendar
import com.tsuchinoko.t2s.core.model.CalendarId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class T2SPreferencesDataStore @Inject constructor(
    private val userPreferences: DataStore<Preferences>,
) {
    fun getTargetCalendarId(): Flow<CalendarId?> = userPreferences.data.map { preferences ->
        val id = preferences[stringPreferencesKey(TARGET_CALENDAR_ID)]
        if (id != null) CalendarId(id) else null
    }

    suspend fun setTargetCalendar(calendar: Calendar) {
        userPreferences.edit { preferences ->
            preferences[stringPreferencesKey(TARGET_CALENDAR_ID)] = calendar.id.value
        }
    }

    companion object {
        const val TARGET_CALENDAR_ID = "target_calendar_id"
    }
}
