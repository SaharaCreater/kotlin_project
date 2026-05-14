package com.app.dopp.ui_project

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.dopp.physics.ExperimentType

@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    when (authState) {
        is AuthState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        else -> {
            val startDestination = if (authState is AuthState.Authenticated) "app" else "login"
            NavHost(navController = navController, startDestination = startDestination) {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate("app") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                composable("app") {
                    MainAppScreen(
                        outerNavController = navController
                    )
                }

                composable(
                    route = "ar/{experimentType}",
                    arguments = listOf(navArgument("experimentType") { type = NavType.StringType })
                ) { backStackEntry ->
                    val experimentTypeName = backStackEntry.arguments?.getString("experimentType") ?: ""
                    val experimentType = try {
                        ExperimentType.valueOf(experimentTypeName)
                    } catch (_: IllegalArgumentException) {
                        ExperimentType.PENDULUM
                    }
                    val physicsViewModel: PhysicsViewModel = hiltViewModel()
                    ARScreen(
                        experimentType = experimentType,
                        onBackClick = { navController.popBackStack() },
                        onExperimentStarted = { physicsViewModel.markCompleted(experimentType.name) }
                    )
                }
            }
        }
    }
}

@Composable
fun MainAppScreen(outerNavController: NavHostController) {
    val innerNavController = rememberNavController()
    val physicsViewModel: PhysicsViewModel = hiltViewModel()

    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Главная") },
                    selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,
                    onClick = {
                        innerNavController.navigate("home") {
                            popUpTo(innerNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Science, contentDescription = null) },
                    label = { Text("Эксперименты") },
                    selected = currentDestination?.hierarchy?.any { it.route == "experiments" } == true,
                    onClick = {
                        innerNavController.navigate("experiments") {
                            popUpTo(innerNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(24.dp)) },
                    label = { Text("Сканер") },
                    selected = false,
                    onClick = {
                        physicsViewModel.onScanClick(outerNavController)
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Профиль") },
                    selected = currentDestination?.hierarchy?.any { it.route == "profile" } == true,
                    onClick = {
                        innerNavController.navigate("profile") {
                            popUpTo(innerNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                val completedCount by physicsViewModel.completedCount.collectAsStateWithLifecycle()
                val authViewModel: AuthViewModel = hiltViewModel()
                val authState by authViewModel.authState.collectAsStateWithLifecycle()
                val userName = (authState as? AuthState.Authenticated)?.user?.name
                MainScreen(
                    onExperimentsClick = {
                        innerNavController.navigate("experiments") {
                            popUpTo(innerNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                        }
                    },
                    onScannerClick = { physicsViewModel.onScanClick(outerNavController) },
                    completedCount = completedCount,
                    userName = userName
                )
            }

            composable("experiments") {
                val completedIds by physicsViewModel.completedIds.collectAsStateWithLifecycle()
                ExperimentsListScreen(
                    onExperimentSelected = { experimentType ->
                        outerNavController.navigate("ar/${experimentType.name}")
                    },
                    completedIds = completedIds
                )
            }

            composable("profile") {
                ProfileScreen(
                    onLogout = {
                        outerNavController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
