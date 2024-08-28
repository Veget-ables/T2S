package com.tsuchinoko.t2s.feature.schedule

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountGuideScreen
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountViewModel
import com.tsuchinoko.t2s.feature.schedule.gen.ScheduleGenScreen
import com.tsuchinoko.t2s.feature.schedule.input.ScheduleInputScreen
import kotlinx.serialization.Serializable

@Serializable
object ScheduleRoute

@Serializable
object CalendarAccountGuide

@Serializable
internal object ScheduleInput

@Serializable
internal data class ScheduleGen(val prompt: String)

fun NavGraphBuilder.scheduleNavigation(
    controller: NavHostController,
    calendarAccountViewModel: CalendarAccountViewModel,
) {
    navigation<ScheduleRoute>(startDestination = ScheduleInput) {
        composable<CalendarAccountGuide> {
            CalendarAccountGuideScreen(
                calendarAccountViewModel = calendarAccountViewModel,
                onCompleteClick = {
                    controller.popBackStack()
                    controller.navigate(ScheduleInput)
                },
            )
        }

        composable<ScheduleInput> {
            ScheduleInputScreen(
                calendarAccountViewModel = calendarAccountViewModel,
                onGenerateClick = { input ->
                    controller.navigate(ScheduleGen(input))
                },
            )
        }

        composable<ScheduleGen> {
            ScheduleGenScreen(
                calendarAccountViewModel = calendarAccountViewModel,
                onInputEditClick = {
                    controller.popBackStack()
                },
            )
        }
    }
}
