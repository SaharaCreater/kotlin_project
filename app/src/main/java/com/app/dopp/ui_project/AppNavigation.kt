package com.app.dopp.ui_project

import androidx.compose.runtime.Composable
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
        // Main screen (home)
        composable(route = "main") {
            MainScreen(
                onExperimentsClick = {
                    navController.navigate("experiments_list")
                }
            )
        }
        
        // Experiments list screen
        composable(route = "experiments_list") {
            ExperimentsListScreen(
                onExperimentSelected = { experimentType ->
                    navController.navigate("ar/${experimentType.name}")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // AR experiment screen
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
                ExperimentType.PENDULUM // Default fallback
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
