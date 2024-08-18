package com.tsuchinoko.t2s.core.domain

import com.tsuchinoko.t2s.core.data.AccountRepository
import com.tsuchinoko.t2s.core.data.CalendarRepository
import com.tsuchinoko.t2s.core.model.Account
import com.tsuchinoko.t2s.core.model.Calendar
import javax.inject.Inject

class GetAccountCalendarsUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val calendarRepository: CalendarRepository,
) {
    suspend operator fun invoke(account: Account): List<Calendar> {
        accountRepository.setAccount(account)
        return calendarRepository.fetchCalendars()
    }
}
