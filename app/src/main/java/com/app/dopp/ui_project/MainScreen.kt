package com.app.dopp.ui_project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.app.dopp.domain.PhysicsExperiment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: PhysicsViewModel = hiltViewModel() // Hilt автоматически создаст ViewModel
) {
    // Подписываемся на данные из ViewModel
    val experiments by viewModel.experiments.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Физика в AR") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("scanner") }) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = "Сканировать QR")
            }
        }
    ) { padding ->
        // Динамический список (Requirement 2)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(experiments) { experiment ->
                ExperimentCard(experiment) {
                    // При клике можно переходить на детали или сразу в AR
                    navController.navigate("ar/${experiment.modelUrl}")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentCard(experiment: PhysicsExperiment, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = experiment.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = experiment.category,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = experiment.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}