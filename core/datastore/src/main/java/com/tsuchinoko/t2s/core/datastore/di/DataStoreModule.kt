package com.tsuchinoko.t2s.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AccountDataStore

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class CalendarDataStore

private val Context.accountDataStore: DataStore<Preferences> by preferencesDataStore(name = "account_preferences")

private val Context.calendarDataStore: DataStore<Preferences> by preferencesDataStore(name = "calendar_preferences")

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    @AccountDataStore
    fun providesAccountDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.accountDataStore

    @Provides
    @Singleton
    @CalendarDataStore
    fun providesCalendarDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = context.calendarDataStore
}
