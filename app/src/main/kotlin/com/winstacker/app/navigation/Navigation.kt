package com.winstacker.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.winstacker.app.ui.addwin.AddWinScreen
import com.winstacker.app.ui.categories.CategoriesScreen
import com.winstacker.app.ui.home.HomeScreen
import com.winstacker.app.ui.settings.SettingsScreen
import com.winstacker.app.ui.streaks.StreaksScreen
import com.winstacker.app.ui.timeline.TimelineScreen
import com.winstacker.app.viewmodel.Timeframe

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Timeline : Screen("timeline?timeframe={timeframe}&categoryFilter={categoryFilter}") {
        fun createRoute(
            timeframe: Timeframe = Timeframe.WEEK,
            categoryFilter: String = "all"
        ) = "timeline?timeframe=" + timeframe.name + "&categoryFilter=" + categoryFilter
    }
    object AddWin : Screen("add_win?winId={winId}") {
        fun createRoute(winId: Long = -1L) = "add_win?winId=" + winId
    }
    object Streaks : Screen("streaks")
    object Categories : Screen("categories")
    object Settings : Screen("settings")
}

@Composable
fun WinsNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    // Deep links can make any destination the back-stack root, where
    // popBackStack() silently fails. Fall back to Home in that case.
    fun navigateBack() {
        if (!navController.popBackStack()) {
            navController.navigate(Screen.Home.route) { launchSingleTop = true }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onEditWin = { winId -> navController.navigate(Screen.AddWin.createRoute(winId)) }
            )
        }

        composable(Screen.Streaks.route) {
            StreaksScreen(
                onOpenSummary = { tf ->
                    navController.navigate(Screen.Timeline.createRoute(timeframe = tf))
                },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(
            route = Screen.Timeline.route,
            arguments = listOf(
                navArgument("timeframe") {
                    type = NavType.StringType
                    defaultValue = Timeframe.WEEK.name
                },
                navArgument("categoryFilter") {
                    type = NavType.StringType
                    defaultValue = "all"
                }
            )
        ) { backStack ->
            val tfName = backStack.arguments?.getString("timeframe") ?: Timeframe.WEEK.name
            val catFilter = backStack.arguments?.getString("categoryFilter") ?: "all"
            TimelineScreen(
                initialTimeframe = runCatching { Timeframe.valueOf(tfName) }.getOrDefault(Timeframe.WEEK),
                initialCategoryFilter = catFilter,
                onEditWin = { winId -> navController.navigate(Screen.AddWin.createRoute(winId)) },
                onBack = { navigateBack() },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(
            route = Screen.AddWin.route,
            arguments = listOf(
                navArgument("winId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStack ->
            val winId = backStack.arguments?.getLong("winId") ?: -1L
            AddWinScreen(
                winId = if (winId == -1L) null else winId,
                onSaved = { navigateBack() },
                onDeleted = { navigateBack() },
                onBack = { navigateBack() }
            )
        }

        composable(Screen.Categories.route) {
            CategoriesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToCategories = { navController.navigate(Screen.Categories.route) }
            )
        }
    }
}
