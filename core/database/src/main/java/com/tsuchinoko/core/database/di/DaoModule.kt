package com.tsuchinoko.core.database.di

import com.tsuchinoko.core.database.T2SDatabase
import com.tsuchinoko.core.database.dao.AccountDao
import com.tsuchinoko.core.database.dao.CalendarDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {
    @Provides
    fun providesAccountDao(
        database: T2SDatabase,
    ): AccountDao = database.accountDao()

    @Provides
    fun providesCalendarDao(
        database: T2SDatabase,
    ): CalendarDao = database.calendarDao()
}
