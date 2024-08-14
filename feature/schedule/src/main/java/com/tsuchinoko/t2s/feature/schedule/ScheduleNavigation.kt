package com.tsuchinoko.t2s.feature.schedule

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.scheduleNavigation() {
    composable("schedule_gen") {
        ScheduleGenScreen()
    }
}
