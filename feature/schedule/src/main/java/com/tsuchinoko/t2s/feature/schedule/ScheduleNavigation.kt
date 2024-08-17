package com.tsuchinoko.t2s.feature.schedule

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountGuideScreen
import com.tsuchinoko.t2s.feature.schedule.gen.ScheduleGenScreen
import com.tsuchinoko.t2s.feature.schedule.input.ScheduleInputGuideScreen
import com.tsuchinoko.t2s.feature.schedule.input.ScheduleInputScreen
import kotlinx.serialization.Serializable

@Serializable
object CalendarAccountGuide

@Serializable
private object ScheduleInputGuide

@Serializable
data class ScheduleInput(val input: String)

@Serializable
private object ScheduleGen

fun NavGraphBuilder.scheduleNavigation(controller: NavHostController) {
    composable<CalendarAccountGuide> {
        CalendarAccountGuideScreen(
            onCompleteClick = {
                controller.navigate(ScheduleInputGuide)
            },
        )
    }

    composable<ScheduleInputGuide> {
        ScheduleInputGuideScreen(
            onInputClick = { input ->
                controller.navigate(ScheduleInput(input))
            },
        )
    }

    composable<ScheduleInput> { backStackEntry ->
        val args = backStackEntry.toRoute<ScheduleInput>()
        ScheduleInputScreen(
            initialInput = args.input,
            onGenerateClick = {
                controller.navigate(ScheduleGen)
            },
        )
    }

    composable<ScheduleGen> {
        ScheduleGenScreen()
    }
}
