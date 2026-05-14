package com.app.dopp.ui_project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.dopp.physics.ExperimentType

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        composable(route = "main") {
            val viewModel: PhysicsViewModel = hiltViewModel()
            val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()
            MainScreen(
                onExperimentsClick = {
                    navController.navigate("experiments_list")
                },
                isOffline = isOffline
            )
        }

        composable(route = "experiments_list") {
            val viewModel: PhysicsViewModel = hiltViewModel()
            val isOffline by viewModel.isOffline.collectAsStateWithLifecycle()
            ExperimentsListScreen(
                onExperimentSelected = { experimentType ->
                    navController.navigate("ar/${experimentType.name}")
                },
                onBackClick = {
                    navController.popBackStack()
                },
                isOffline = isOffline
            )
        }

        composable(
            route = "ar/{experimentType}",
            arguments = listOf(
                navArgument("experimentType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val experimentTypeName = backStackEntry.arguments?.getString("experimentType") ?: ""
            val experimentType = try {
                ExperimentType.valueOf(experimentTypeName)
            } catch (e: IllegalArgumentException) {
                ExperimentType.PENDULUM
            }

            ARScreen(
                experimentType = experimentType,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
