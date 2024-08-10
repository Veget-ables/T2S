package com.tsuchinoko.t2s.feature.schedule.data

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
