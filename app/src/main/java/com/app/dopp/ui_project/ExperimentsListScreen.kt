package com.app.dopp.ui_project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun ExperimentsListScreen(
    navController: NavHostController,
    viewModel: PhysicsViewModel = hiltViewModel()
) {
    // Получаем список экспериментов из ViewModel
    val experiments by viewModel.experiments.collectAsState()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.onScanClick(navController) },
                icon = { /* Можно добавить иконку здесь */ },
                text = { Text(text = "Сканировать QR") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(experiments) { experiment ->
                ExperimentItem(experiment)
            }
        }
    }
}

@Composable
fun ExperimentItem(experiment: com.app.dopp.domain.PhysicsExperiment) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = experiment.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = experiment.description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}