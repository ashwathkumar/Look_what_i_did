package com.winstacker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
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

            val darkTheme = when (appTheme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            WinsTrackerTheme(darkTheme = darkTheme) {
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
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Screen.Home.route,
        label = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = Screen.AddWin.createRoute(),
        label = "Log a Win",
        selectedIcon = Icons.Filled.AddCircle,
        unselectedIcon = Icons.Outlined.AddCircleOutline
    ),
    BottomNavItem(
        route = Screen.Timeline.createRoute(),
        label = "Timeline",
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart
    )
)

@Composable
fun WinsTrackerApp(startRoute: String? = null) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Routes where bottom nav is visible
    val bottomNavRoutes = setOf(
        Screen.Home.route,
        Screen.Timeline.createRoute().substringBefore("?")
    )

    val showBottomBar = currentDestination?.route?.let { route ->
        bottomNavRoutes.any { route.startsWith(it) } || route.startsWith("timeline")
    } ?: false

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { dest ->
                            dest.route?.startsWith(item.route.substringBefore("?")) == true
                        } == true

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
        Box(modifier = Modifier.padding(innerPadding)) {
            WinsNavHost(
                navController = navController,
                startDestination = startRoute ?: Screen.Home.route
            )
        }
    }
}
