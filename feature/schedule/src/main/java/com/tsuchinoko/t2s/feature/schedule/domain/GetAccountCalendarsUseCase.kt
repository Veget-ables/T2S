package com.tsuchinoko.t2s.feature.schedule.domain

import com.tsuchinoko.t2s.feature.schedule.data.AccountRepository
import com.tsuchinoko.t2s.feature.schedule.data.CalendarRepository
import com.tsuchinoko.t2s.feature.schedule.model.Calendar
import javax.inject.Inject

class GetAccountCalendarsUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val calendarRepository: CalendarRepository,
) {
    suspend operator fun invoke(accountName: String): List<Calendar>{
        accountRepository.setAccountName(accountName)
        return calendarRepository.fetchCalendars()
    }
}