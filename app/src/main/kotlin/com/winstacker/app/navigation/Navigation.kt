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
import com.winstacker.app.ui.stats.StatsScreen
import com.winstacker.app.ui.timeline.TimelineScreen
import com.winstacker.app.viewmodel.Timeframe

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Timeline : Screen("timeline?timeframe={timeframe}&categoryFilter={categoryFilter}") {
        fun createRoute(
            timeframe: Timeframe = Timeframe.WEEK,
            categoryFilter: String = "all"
        ) = "timeline?timeframe=${timeframe.name}&categoryFilter=$categoryFilter"
    }
    object AddWin : Screen("add_win?winId={winId}") {
        fun createRoute(winId: Long = -1L) = "add_win?winId=$winId"
    }
    object Stats : Screen("stats")
    object Categories : Screen("categories")
    object Settings : Screen("settings")
}

@Composable
fun WinsNavHost(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToTimeline = { tf ->
                    navController.navigate(Screen.Timeline.createRoute(timeframe = tf))
                },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToStats = { navController.navigate(Screen.Stats.route) }
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
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToStats = { navController.navigate(Screen.Stats.route) }
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
                onSaved = { navController.popBackStack() },
                onDeleted = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen()
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
