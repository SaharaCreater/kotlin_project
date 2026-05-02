package com.app.dopp.ui_project

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.dopp.physics.ExperimentCategory
import com.app.dopp.physics.ExperimentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentsListScreen(
    onExperimentSelected: (ExperimentType) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<ExperimentCategory?>(null) }
    
    val filteredExperiments = remember(selectedCategory) {
        if (selectedCategory == null) {
            ExperimentType.entries
        } else {
            ExperimentType.entries.filter { it.category == selectedCategory }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Физические эксперименты",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category filter chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Все") },
                        leadingIcon = if (selectedCategory == null) {
                            { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                        } else null
                    )
                }
                items(ExperimentCategory.entries) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category.displayName) },
                        leadingIcon = if (selectedCategory == category) {
                            { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                        } else {
                            { Icon(getCategoryIcon(category), contentDescription = null, Modifier.size(18.dp)) }
                        }
                    )
                }
            }
            
            // Experiments list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Group by category if no filter selected
                if (selectedCategory == null) {
                    ExperimentCategory.entries.forEach { category ->
                        val categoryExperiments = filteredExperiments.filter { it.category == category }
                        if (categoryExperiments.isNotEmpty()) {
                            item {
                                CategoryHeader(category)
                            }
                            items(categoryExperiments) { experiment ->
                                ExperimentCard(
                                    experiment = experiment,
                                    onClick = { onExperimentSelected(experiment) }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                } else {
                    items(filteredExperiments) { experiment ->
                        ExperimentCard(
                            experiment = experiment,
                            onClick = { onExperimentSelected(experiment) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(category: ExperimentCategory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = getCategoryColor(category).copy(alpha = 0.15f)
        ) {
            Icon(
                imageVector = getCategoryIcon(category),
                contentDescription = null,
                modifier = Modifier.padding(8.dp),
                tint = getCategoryColor(category)
            )
        }
        Column {
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = category.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExperimentCard(
    experiment: ExperimentType,
    onClick: () -> Unit
) {
    val categoryColor = getCategoryColor(experiment.category)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with gradient background
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                categoryColor.copy(alpha = 0.8f),
                                categoryColor.copy(alpha = 0.5f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getExperimentIcon(experiment),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
            }
            
            // Text content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = experiment.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = experiment.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            
            // Arrow indicator
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Открыть",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getCategoryIcon(category: ExperimentCategory): ImageVector {
    return when (category) {
        ExperimentCategory.MECHANICS -> Icons.Default.Settings
        ExperimentCategory.ELECTRICITY -> Icons.Default.Bolt
        ExperimentCategory.OPTICS -> Icons.Default.Lightbulb
        ExperimentCategory.THERMODYNAMICS -> Icons.Default.Whatshot
    }
}

private fun getCategoryColor(category: ExperimentCategory): Color {
    return when (category) {
        ExperimentCategory.MECHANICS -> Color(0xFF2196F3) // Blue
        ExperimentCategory.ELECTRICITY -> Color(0xFFFFC107) // Amber
        ExperimentCategory.OPTICS -> Color(0xFF9C27B0) // Purple
        ExperimentCategory.THERMODYNAMICS -> Color(0xFFFF5722) // Deep Orange
    }
}

private fun getExperimentIcon(experiment: ExperimentType): ImageVector {
    return when (experiment) {
        ExperimentType.PENDULUM -> Icons.Default.SwapVert
        ExperimentType.FREE_FALL -> Icons.Default.ArrowDownward
        ExperimentType.COLLISION -> Icons.Default.Compress
        ExperimentType.ELECTRIC_CIRCUIT -> Icons.Default.Cable
        ExperimentType.MAGNETIC_FIELD -> Icons.Default.Radar
        ExperimentType.LIGHT_REFRACTION -> Icons.Default.Flare
        ExperimentType.LENS -> Icons.Default.CenterFocusWeak
        ExperimentType.BROWNIAN_MOTION -> Icons.Default.Grain
        ExperimentType.GAS_EXPANSION -> Icons.Default.Air
    }
}
