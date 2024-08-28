package com.tsuchinoko.t2s

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.feature.schedule.CalendarAccountGuide
import com.tsuchinoko.t2s.feature.schedule.ScheduleRoute
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountViewModel
import com.tsuchinoko.t2s.feature.schedule.account.GetAccountPermissionEffect
import com.tsuchinoko.t2s.feature.schedule.scheduleNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val calendarAccountViewModel: CalendarAccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            T2STheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = ScheduleRoute,
                ) {
                    scheduleNavigation(
                        controller = navController,
                        calendarAccountViewModel = calendarAccountViewModel,
                    )
                }
                val needAccount by calendarAccountViewModel.needAccount.collectAsState()
                LaunchedEffect(needAccount) {
                    if (needAccount) {
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(
                                route = ScheduleRoute,
                                inclusive = true,
                            ).build()
                        navController.navigate(
                            route = CalendarAccountGuide,
                            navOptions = navOptions,
                        )
                    }
                }
                GetAccountPermissionEffect()
            }
        }
    }
}
