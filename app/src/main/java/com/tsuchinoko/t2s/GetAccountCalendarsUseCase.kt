package com.tsuchinoko.t2s

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