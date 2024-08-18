package com.tsuchinoko.core.database.di

import android.content.Context
import androidx.room.Room
import com.tsuchinoko.core.database.T2SDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesT2SDatabase(
        @ApplicationContext context: Context,
    ): T2SDatabase = Room.databaseBuilder(
        context = context,
        klass = T2SDatabase::class.java,
        name = "t2s-database",
    ).build()
}
