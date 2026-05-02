package com.app.dopp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.app.dopp.ui.theme.DoPPTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DoPPTheme {
                val navController = rememberNavController()
                // AppNavigation пока будет гореть красным, мы его создадим следующим шагом
                AppNavigation(navController)
            }
        }
    }
}