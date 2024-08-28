package com.tsuchinoko.t2s

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.tsuchinoko.t2s.core.designsystem.them.T2STheme
import com.tsuchinoko.t2s.feature.schedule.ScheduleRoute
import com.tsuchinoko.t2s.feature.schedule.account.GetAccountPermissionEffect
import com.tsuchinoko.t2s.feature.schedule.scheduleNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
                    scheduleNavigation(navController)
                }
                GetAccountPermissionEffect()
            }
        }
    }
}
