package com.tsuchinoko.t2s.core.domain

import com.tsuchinoko.t2s.core.data.AccountRepository
import com.tsuchinoko.t2s.core.data.CalendarRepository
import com.tsuchinoko.t2s.core.model.Account
import javax.inject.Inject

class GetAccountCalendarsUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val calendarRepository: CalendarRepository,
) {
    suspend operator fun invoke(account: Account) {
        accountRepository.setAccount(account)
        calendarRepository.fetchCalendars()
    }
}
