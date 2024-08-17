package com.tsuchinoko.t2s.feature.schedule

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountGuideScreen
import com.tsuchinoko.t2s.feature.schedule.guide.ScheduleInputGuideScreen
import kotlinx.serialization.Serializable

@Serializable
object CalendarAccountGuide

@Serializable
private object ScheduleInputGuide

@Serializable
private object ScheduleGen

fun NavGraphBuilder.scheduleNavigation(controller: NavHostController) {
    composable<CalendarAccountGuide> {
        CalendarAccountGuideScreen(onCompleteClick = {
            controller.navigate(ScheduleInputGuide)
        })
    }

    composable<ScheduleInputGuide> {
        ScheduleInputGuideScreen()
    }

    composable<ScheduleGen> {
        ScheduleGenScreen()
    }
}
