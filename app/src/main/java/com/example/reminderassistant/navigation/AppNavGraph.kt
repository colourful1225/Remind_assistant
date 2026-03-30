package com.example.reminderassistant.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.reminderassistant.ui.home.HomeScreen
import com.example.reminderassistant.ui.importflow.ImportConfirmScreen
import com.example.reminderassistant.ui.reminder.ReminderEditorScreen
import com.example.reminderassistant.ui.calendar.CalendarEditorScreen
import com.example.reminderassistant.ui.settings.SettingsScreen

/**
 * Navigation graph for the app
 */
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToImportConfirm = {
                    navController.navigate(Routes.IMPORT_CONFIRM)
                },
                onNavigateToReminderEditor = {
                    navController.navigate(Routes.REMINDER_EDITOR)
                },
                onNavigateToCalendarEditor = {
                    navController.navigate(Routes.CALENDAR_EDITOR)
                },
                onNavigateToSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                viewModel = hiltViewModel()
            )
        }

        composable(Routes.IMPORT_CONFIRM) {
            ImportConfirmScreen(
                onBack = {
                    navController.popBackStack()
                },
                viewModel = hiltViewModel()
            )
        }

        composable(Routes.REMINDER_EDITOR) {
            ReminderEditorScreen(
                onBack = {
                    navController.popBackStack()
                },
                viewModel = hiltViewModel()
            )
        }

        composable(Routes.CALENDAR_EDITOR) {
            CalendarEditorScreen(
                onBack = {
                    navController.popBackStack()
                },
                viewModel = hiltViewModel()
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = {
                    navController.popBackStack()
                },
                viewModel = hiltViewModel()
            )
        }
    }
}
