package com.tsuchinoko.t2s.core.data_google

import com.tsuchinoko.t2s.core.data.AccountRepository
import com.tsuchinoko.t2s.core.data.CalendarRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class GoogleRepositoryModule {
    @Binds
    abstract fun bindAccountRepository(impl: GoogleAccountRepository): AccountRepository

    @Binds
    abstract fun bindCalendarRepository(impl: GoogleCalendarRepository): CalendarRepository
}
