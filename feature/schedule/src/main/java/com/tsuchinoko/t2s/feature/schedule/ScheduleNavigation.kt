package com.tsuchinoko.t2s.feature.schedule

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountGuideScreen
import com.tsuchinoko.t2s.feature.schedule.input.ScheduleInputGuideScreen
import com.tsuchinoko.t2s.feature.schedule.input.ScheduleInputScreen
import kotlinx.serialization.Serializable

@Serializable
object CalendarAccountGuide

@Serializable
private object ScheduleInputGuide

@Serializable
private object ScheduleInput

@Serializable
private object ScheduleGen

fun NavGraphBuilder.scheduleNavigation(controller: NavHostController) {
    composable<CalendarAccountGuide> {
        CalendarAccountGuideScreen(onCompleteClick = {
            controller.navigate(ScheduleInputGuide)
        })
    }

    composable<ScheduleInputGuide> {
        ScheduleInputGuideScreen(onInputClick = {
            controller.navigate(ScheduleInput)
        })
    }

    composable<ScheduleInput> {
        ScheduleInputScreen(onGenerateClick = {
            controller.navigate(ScheduleGen)
        })
    }

    composable<ScheduleGen> {
        ScheduleGenScreen()
    }
}
