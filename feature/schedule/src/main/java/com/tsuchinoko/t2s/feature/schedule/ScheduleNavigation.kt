package com.tsuchinoko.t2s.feature.schedule

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountGuideScreen
import com.tsuchinoko.t2s.feature.schedule.account.CalendarAccountViewModel
import com.tsuchinoko.t2s.feature.schedule.gen.ScheduleGenScreen
import com.tsuchinoko.t2s.feature.schedule.input.ScheduleInputGuideScreen
import com.tsuchinoko.t2s.feature.schedule.input.ScheduleInputScreen
import kotlinx.serialization.Serializable

@Serializable
object ScheduleRoute

@Serializable
object CalendarAccountGuide

@Serializable
private object ScheduleInputGuide

@Serializable
data class ScheduleInput(val input: String)

@Serializable
private object ScheduleGen

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

            val routeEntry = remember(backStackEntry) {
                controller.getBackStackEntry(ScheduleRoute)
            }
            val calendarAccountViewModel = hiltViewModel<CalendarAccountViewModel>(routeEntry)
            ScheduleInputScreen(
                initialInput = args.input,
                calendarAccountViewModel = calendarAccountViewModel,
                onGenerateClick = {
                    controller.navigate(ScheduleGen)
                },
            )
        }

        composable<ScheduleGen> { backStackEntry ->
            val routeEntry = remember(backStackEntry) {
                controller.getBackStackEntry(ScheduleRoute)
            }
            val calendarAccountViewModel = hiltViewModel<CalendarAccountViewModel>(routeEntry)
            ScheduleGenScreen(calendarAccountViewModel = calendarAccountViewModel)
        }
    }
}
