package com.app.dopp.ui_project

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "experiments_list" // Начальный экран приложения
    ) {
        // Экрана со списком экспериментов
        composable(route = "experiments_list") {
            // Твой основной экран со списком опытов
            ExperimentsListScreen(navController = navController)
        }

        // Экран AR с передачей URL модели через аргументы
        composable(
            route = "ar/{modelUrl}",
            arguments = listOf(
                navArgument("modelUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Извлекаем URL модели из аргументов навигации
            val modelUrl = backStackEntry.arguments?.getString("modelUrl") ?: ""
            // Вызываем ARScreen, передавая полученную ссылку
            ARScreen(modelUrl = modelUrl)
        }
    }
}