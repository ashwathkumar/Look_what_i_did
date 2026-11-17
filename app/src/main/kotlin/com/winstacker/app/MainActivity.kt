package com.winstacker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.winstacker.app.data.repository.AppTheme
import com.winstacker.app.navigation.Screen
import com.winstacker.app.navigation.WinsNavHost
import com.winstacker.app.ui.theme.WinsTrackerTheme
import com.winstacker.app.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val deepLinkRoute = resolveDeepLinkRoute(intent?.data?.toString())

        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val appTheme by settingsViewModel.appTheme.collectAsStateWithLifecycle()
            val appAccent by settingsViewModel.appAccent.collectAsStateWithLifecycle()

            val darkTheme = when (appTheme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            WinsTrackerTheme(darkTheme = darkTheme, accent = appAccent) {
                WinsTrackerApp(startRoute = deepLinkRoute)
            }
        }
    }

    private fun resolveDeepLinkRoute(deepLink: String?): String? {
        return when {
            deepLink?.startsWith("winstacker://add") == true ->
                Screen.AddWin.createRoute()
            deepLink?.startsWith("winstacker://weekly") == true -> {
                val uri = android.net.Uri.parse(deepLink)
                val catFilter = uri.getQueryParameter("categoryFilter") ?: "all"
                Screen.Timeline.createRoute(
                    timeframe = com.winstacker.app.viewmodel.Timeframe.WEEK,
                    categoryFilter = catFilter
                )
            }
            deepLink?.startsWith("winstacker://timeline") == true -> {
                val uri = android.net.Uri.parse(deepLink)
                val tfName = uri.getQueryParameter("timeframe") ?: "WEEK"
                val catFilter = uri.getQueryParameter("categoryFilter") ?: "all"
                val tf = runCatching { com.winstacker.app.viewmodel.Timeframe.valueOf(tfName) }.getOrDefault(com.winstacker.app.viewmodel.Timeframe.WEEK)
                Screen.Timeline.createRoute(
                    timeframe = tf,
                    categoryFilter = catFilter
                )
            }
            else -> null
        }
    }
}

data class BottomNavItem(
    /** Route to navigate to when the tab is tapped. */
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    /** Route prefixes that should light this tab up. */
    val matchPrefixes: List<String>
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Home.route,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        matchPrefixes = listOf("home")
    ),
    BottomNavItem(
        route = Screen.AddWin.createRoute(),
        label = "Log a Win",
        selectedIcon = Icons.Filled.AddCircle,
        unselectedIcon = Icons.Outlined.AddCircleOutline,
        matchPrefixes = listOf("add_win")
    ),
    BottomNavItem(
        route = Screen.Streaks.route,
        label = "Streaks",
        selectedIcon = Icons.Filled.LocalFireDepartment,
        unselectedIcon = Icons.Outlined.LocalFireDepartment,
        // Timeline is the summary drilled into from a Streaks row, so it keeps
        // this tab selected.
        matchPrefixes = listOf("streaks", "timeline")
    )
)

@Composable
fun WinsTrackerApp(startRoute: String? = null) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route.orEmpty()

    // Routes where the bottom nav is visible
    val showBottomBar = listOf("home", "streaks", "timeline").any { currentRoute.startsWith(it) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val isSelected = item.matchPrefixes.any { currentRoute.startsWith(it) }

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // Pad for the bottom bar and status bar, then mark those insets as
        // consumed so each screen's own top bar does not add them a second time.
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            WinsNavHost(
                navController = navController,
                startDestination = startRoute ?: Screen.Home.route
            )
        }
    }
}
