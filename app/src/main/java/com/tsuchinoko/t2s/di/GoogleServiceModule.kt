package com.tsuchinoko.t2s.di

import android.content.Context
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class GoogleServiceModule {

    @Provides
    fun provideGoogleAccountCredential(@ApplicationContext context: Context): GoogleAccountCredential {
        return GoogleAccountCredential
            .usingOAuth2(
                context,
                arrayListOf(CalendarScopes.CALENDAR)
            )
            .setBackOff(ExponentialBackOff())
    }

    @Provides
    fun provideGoogleCalendarService(credential: GoogleAccountCredential): Calendar {
        return Calendar.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("T2S")
            .build()
    }
}
