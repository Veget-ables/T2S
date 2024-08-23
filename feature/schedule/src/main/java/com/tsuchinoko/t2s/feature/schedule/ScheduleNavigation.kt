package com.tsuchinoko.t2s.feature.schedule

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
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
data object ScheduleInput

@Serializable
internal data class ScheduleGen(val prompt: String)

fun NavGraphBuilder.scheduleNavigation(controller: NavHostController) {
    navigation<ScheduleRoute>(startDestination = CalendarAccountGuide) {
        composable<CalendarAccountGuide> { backStackEntry ->
            val routeEntry = remember(backStackEntry) {
                controller.getBackStackEntry(ScheduleRoute)
            }
            val calendarAccountViewModel = hiltViewModel<CalendarAccountViewModel>(routeEntry)
            CalendarAccountGuideScreen(
                calendarAccountViewModel = calendarAccountViewModel,
                onCompleteClick = {
                    controller.navigate(ScheduleInput)
                },
            )
        }

        composable<ScheduleInput> { backStackEntry ->
            val routeEntry = remember(backStackEntry) {
                controller.getBackStackEntry(ScheduleRoute)
            }
            val calendarAccountViewModel = hiltViewModel<CalendarAccountViewModel>(routeEntry)
            ScheduleInputScreen(
                calendarAccountViewModel = calendarAccountViewModel,
                onGenerateClick = { input ->
                    controller.navigate(ScheduleGen(input))
                },
            )
        }

        composable<ScheduleGen> { backStackEntry ->
            val routeEntry = remember(backStackEntry) {
                controller.getBackStackEntry(ScheduleRoute)
            }
            val calendarAccountViewModel = hiltViewModel<CalendarAccountViewModel>(routeEntry)
            ScheduleGenScreen(
                calendarAccountViewModel = calendarAccountViewModel,
                onInputEditClick = {
                    controller.popBackStack()
                },
            )
        }
    }
}
