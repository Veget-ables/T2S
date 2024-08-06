package com.tsuchinoko.t2s.di

import com.tsuchinoko.t2s.AccountRepository
import com.tsuchinoko.t2s.AccountRepositoryImpl
import com.tsuchinoko.t2s.CalendarRepository
import com.tsuchinoko.t2s.CalendarRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    abstract fun bindCalendarRepository(impl: CalendarRepositoryImpl): CalendarRepository
}
